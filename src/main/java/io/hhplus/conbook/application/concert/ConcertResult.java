package io.hhplus.conbook.application.concert;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class ConcertResult {
    public record Search(
            long concertId,
            String title,
            LocalDate date,
            int soldCount,
            int capacity
    ) {}

    public record Status(
            long concertId,
            String concertTitle,
            LocalDate date,
            List<SeatInfo> seatInfo
    ) {}

    @AllArgsConstructor
    @Getter
    public static class SeatInfo {
        private long id;
        private String rowName;
        private int seatNo;
    }

    public record BookingDto (
            long bookingId,
            String userName,
            String rowName,
            int seatNo,
            LocalDateTime bookingDateTime,
            LocalDateTime expirationTime
    ) {}
}
