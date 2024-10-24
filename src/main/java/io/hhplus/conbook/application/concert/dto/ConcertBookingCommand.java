package io.hhplus.conbook.application.concert.dto;

import static io.hhplus.conbook.application.concert.DateValidator.validateDate;

public class ConcertBookingCommand {
    public record BookingSeat(
            long concertId,
            String date,
            String userUUID,
            long seatId
    ) {
        public BookingSeat {
            validateDate(date);

            if (seatId <= 0) throw new IllegalArgumentException();
        }
    }
}
