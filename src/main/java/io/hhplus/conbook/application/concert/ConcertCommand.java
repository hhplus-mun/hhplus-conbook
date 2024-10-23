package io.hhplus.conbook.application.concert;

import io.hhplus.conbook.interfaces.api.ErrorCode;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

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

    public record Booking(
            long concertId,
            String date,
            String userUUID,
            long seatId
    ) {
       public Booking {
           validateDate(date);

           if (seatId <= 0) throw new IllegalArgumentException();
       }
    }

    private static void validateDate(String date) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            LocalDate.parse(date, formatter);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(ErrorCode.CONCERT_DATE_FOMRAT.getCode());
        }
    }
}
