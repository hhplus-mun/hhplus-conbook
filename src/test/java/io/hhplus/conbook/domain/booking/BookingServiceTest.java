package io.hhplus.conbook.domain.booking;

import io.hhplus.conbook.domain.concert.ConcertSchedule;
import io.hhplus.conbook.domain.concert.ConcertService;
import io.hhplus.conbook.domain.user.User;
import io.hhplus.conbook.domain.user.UserService;
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
    SeatJpaRepository seatJpaRepository;
    @Autowired
    BookingJpaRepository bookingJpaRepository;

    @AfterEach
    @Transactional
    void clear() {
        System.out.println("*** 예약정보 초기화 ***");

        bookingJpaRepository.deleteAll();

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
}