package io.hhplus.conbook.interfaces.api.concert;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class ConcertResponse {
    public record AvailableDates (
            long concertId,
            String concertName,
            List<ConcertDetails> dates
    ) {}

    public record AvailableSeats (
            long concertId,
            String concertName,
            LocalDate date,
            List<SeatDetails> seats
    ) {}

    public record Booking (
            long bookingId,
            String userName,
            String rowName,
            int seatNo,
            LocalDateTime bookingDateTime,
            LocalDateTime expirationTime
    ) {}

    @AllArgsConstructor
    @Getter
    public static class ConcertDetails {
        private LocalDate date;
        private long capacity;
    }

    @AllArgsConstructor
    @Getter
    public static class SeatDetails {
        private long id;
        private String rowName;
        private int seatNo;
    }
}
