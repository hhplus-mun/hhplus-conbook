package io.hhplus.conbook.application.booking;

import io.hhplus.conbook.domain.booking.Booking;
import io.hhplus.conbook.domain.booking.BookingService;
import io.hhplus.conbook.domain.booking.BookingStatus;
import io.hhplus.conbook.domain.concert.ConcertSchedule;
import io.hhplus.conbook.domain.concert.ConcertScheduleRepository;
import io.hhplus.conbook.domain.concert.Seat;
import io.hhplus.conbook.domain.concert.SeatRepository;
import io.hhplus.conbook.domain.user.User;
import io.hhplus.conbook.domain.user.UserService;
import io.hhplus.conbook.infra.db.booking.BookingEntity;
import io.hhplus.conbook.infra.db.booking.BookingJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
class BookingPaymentFacadeTest {

    @Autowired
    BookingPaymentFacade bookingPaymentFacade;

    @Autowired
    ConcertScheduleRepository concertScheduleRepository;
    @Autowired
    SeatRepository seatRepository;
    @Autowired
    UserService userService;
    @Autowired
    BookingService bookingService;

    @Autowired
    BookingJpaRepository bookingJpaRepository;


    @Test
    @DisplayName("[정상]: 예약건 정상 결제")
    void processPaymentForBooking() {
        // given
        long concertId = 1L;
        long userId = 1L;
        LocalDate date = LocalDate.of(2024, Month.OCTOBER, 1);
        ConcertSchedule schedule = concertScheduleRepository.findScheduleBy(concertId, date);
        Seat seat = seatRepository.findAvailableListBy(schedule.getId())
                .get(0);
        User user = userService.getUser(userId);
        Booking booking = bookingService.createBooking(schedule, seat.getId(), user);

        // when
        BookingPaymentCommand.Paid paidCommand = BookingPaymentCommand.Paid.builder()
                .bookingId(booking.getId())
                .concertId(concertId)
                .userUUID(user.getUuid())
                .reqTime(LocalDateTime.now())
                .build();
        System.out.println("paidCommand = " + paidCommand);

        BookingPaymentResult.Paid result = bookingPaymentFacade.processPayment(paidCommand);

        // then
        long postBookingId = result.bookingId();
        BookingEntity bookingEntity = bookingJpaRepository.findById(postBookingId).get();

        assertAll(
                () -> assertThat(bookingEntity.getStatus()).isEqualTo(BookingStatus.PAID)
        );
    }
}