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
import io.hhplus.conbook.infra.db.payment.PaymentJpaRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
class BookingServiceTest {
    @Autowired
    BookingService bookingService;

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
}