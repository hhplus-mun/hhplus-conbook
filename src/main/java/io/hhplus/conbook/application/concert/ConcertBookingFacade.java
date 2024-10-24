package io.hhplus.conbook.application.concert;

import io.hhplus.conbook.application.concert.dto.ConcertBookingCommand;
import io.hhplus.conbook.application.concert.dto.ConcertBookingResult;
import io.hhplus.conbook.domain.booking.Booking;
import io.hhplus.conbook.domain.booking.BookingService;
import io.hhplus.conbook.domain.concert.ConcertSchedule;
import io.hhplus.conbook.domain.concert.ConcertService;
import io.hhplus.conbook.domain.user.User;
import io.hhplus.conbook.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ConcertBookingFacade {

    private final UserService userService;
    private final ConcertService concertService;
    private final BookingService bookingService;

    /**
     * 각 공연은 모두 좌석을 생성한다고 가정.
     * - 좌석을 미리 안 만들 경우 어떤 식으로 처리할 지는 추후 여유가 있을 때 구현
     *  (좌석의 좌표값(x,y)만 제공될 경우)
     */
    @Transactional
    public ConcertBookingResult.BookingSeat bookConcertSeat(ConcertBookingCommand.BookingSeat booking) {
        User user = userService.getUserByUUID(booking.userUUID());

        // scheduleId, seat 정보,
        ConcertSchedule concertSchedule = concertService.getConcertSchedule(booking.concertId(), booking.date());
        Booking bookingResult = bookingService.createBooking(concertSchedule, booking.seatId(), user);
        bookingService.addSchedule(bookingResult);

        concertService.updateSeatStatus(booking.concertId(), booking.date());

        return ConcertBookingResult.BookingSeat.builder()
                .bookingId(bookingResult.getId())
                .userName(user.getName())
                .rowName(bookingResult.getSeat().getRowName())
                .seatNo(bookingResult.getSeat().getSeatNo())
                .bookingDateTime(bookingResult.getCreatedAt())
                .expirationTime(bookingResult.getExpiredAt())
                .build();
    }
}
