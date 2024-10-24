package io.hhplus.conbook.application.concert.dto;

import static io.hhplus.conbook.application.concert.DateValidator.validateDate;

public class ConcertCommand {
    public record Search(
            long concertId
    ) {}

    public record Status(
            long concertId,
            String date
    ) {
        public Status {
            validateDate(date);
        }
    }
}
