package io.hhplus.conbook.domain.booking;

import io.hhplus.conbook.domain.concert.ConcertSchedule;
import io.hhplus.conbook.domain.concert.ConcertService;
import io.hhplus.conbook.domain.concert.Seat;
import io.hhplus.conbook.domain.concert.SeatRepository;
import io.hhplus.conbook.domain.user.User;
import io.hhplus.conbook.domain.user.UserService;
import io.hhplus.conbook.infra.db.booking.BookingEntity;
import io.hhplus.conbook.infra.db.booking.BookingJpaRepository;
import io.hhplus.conbook.infra.db.concert.SeatEntity;
import io.hhplus.conbook.infra.db.concert.SeatJpaRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static java.util.concurrent.CompletableFuture.runAsync;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
class BookingServiceTest {
    // test target
    @Autowired
    BookingService bookingService;

    // helpers
    @Autowired
    UserService userService;
    @Autowired
    ConcertService concertService;
    @Autowired
    SeatRepository seatRepository;
    @Autowired
    SeatJpaRepository seatJpaRepository;
    @Autowired
    BookingJpaRepository bookingJpaRepository;

    // =============================================================================================

    @AfterEach
    @Transactional
    void clear() {
        System.out.println("*** 예약정보 초기화 ***");

        List<BookingEntity> deleteTarget = bookingJpaRepository.findAll()
                .stream()
                .filter(b -> b.getStatus().equals(BookingStatus.RESERVED))
                .toList();
        bookingJpaRepository.deleteAll(deleteTarget);

        List<SeatEntity> seatList = seatJpaRepository.findAll();
        for (SeatEntity seat : seatList) {
            ReflectionTestUtils.setField(seat, "isOccupied", false);
            seatJpaRepository.save(seat);
        }
    }

    @Test
    @DisplayName("[정상]: 통합테스트 - 좌석 예약")
    void createBooking() {
        // given
        long seatId = 1L;
        long concertId = 1L;
        String date = "20241001";
        User user = userService.getUser(1L);

        ConcertSchedule schedule = concertService.getConcertSchedule(concertId, date);

        // when
        Booking booking = bookingService.createBooking(schedule, seatId, user);

        // then
        SeatEntity seatEntity = seatJpaRepository.findById(seatId).get();

        assertAll(
                () -> assertThat(seatEntity.isOccupied()).isTrue(),
                () -> assertThat(booking.getUser().getUuid()).isEqualTo(user.getUuid())
        );
    }

    @Test
    @DisplayName("[예외]: 통합테스트 - 중복 예약신청")
    void alreadyOccupied() {
        // given
        long seatId = 1L;
        long concertId = 1L;
        String date = "20241001";
        User user = userService.getUser(1L);
        ConcertSchedule schedule = concertService.getConcertSchedule(concertId, date);

        bookingService.createBooking(schedule, seatId, user);

        // when & then
        assertThatThrownBy(() -> bookingService.createBooking(schedule, seatId, user))
                .isInstanceOf(AlreadyOccupiedException.class);
    }
    
    @Test
    @DisplayName("[정상]: 좌석 상태 초기화 - 결제 없이 일정시간이 지나면 좌석 점유상태 해제")
    void checkOrUpdate() {
        // given
        long userId = 1L;
        long concertId = 1L;
        String date = "20241001";

        User user = userService.getUser(userId);
        ConcertSchedule schedule = concertService.getConcertSchedule(concertId, date);
        Seat seat = seatRepository.findAvailableListBy(schedule.getId()).get(0);
        Booking booking = bookingService.createBooking(schedule, seat.getId(), user);

        // when
        bookingService.checkOrUpdate(booking.getId());

        // then
        BookingEntity bookingEntity = bookingJpaRepository.findById(booking.getId()).get();
        SeatEntity seatEntity = seatJpaRepository.findById(seat.getId()).get();

        assertAll(
                () -> assertThat(seatEntity.isOccupied()).isFalse(),
                () -> assertThat(bookingEntity.getStatus()).isEqualTo(BookingStatus.CANCELLED)
        );
    }

    // =============================================================================================

    @Test
    @DisplayName("[동시성]: 콘서트 예약 - 2명 이상의 사용자가 동일한 좌석예약을 시도하는 시나리오")
    void tryToMakeBookingForSameSeat() {
        // given
        long concertId = 1L;
        long userId1 = 1L;
        long userId2 = 2L;
        String date = "20241001";
        ConcertSchedule schedule = concertService.getConcertSchedule(concertId, date);
        User user1 = userService.getUser(userId1);
        User user2 = userService.getUser(userId2);
        List<User> users = Arrays.asList(user1, user2);
        Seat seat = seatRepository.findAvailableListBy(schedule.getId()).get(0);

        // when
        List<Runnable> tasks = new ArrayList<>();
        for (User user : users) {
            tasks.add(() -> {
                try {
                    bookingService.createBooking(schedule, seat.getId(), user);
                } catch (AlreadyOccupiedException e) {
                    System.out.printf("[FAIL] 예약 실패 - 사용자 id: %d, 좌석 id: %d\n", user.getId(), seat.getId());
                }
            });
        }

        CompletableFuture allTask =
                CompletableFuture.allOf(
                        tasks.stream().map(task -> runAsync(task)).toArray(CompletableFuture[]::new)
                );
        allTask.join();

        // then
        List<BookingEntity> bookingList = bookingJpaRepository.findAll().stream()
                .filter(b -> b.getSeat().getId().equals(seat.getId()))
                .toList();

        assertThat(bookingList.size()).isEqualTo(1);
    }
}