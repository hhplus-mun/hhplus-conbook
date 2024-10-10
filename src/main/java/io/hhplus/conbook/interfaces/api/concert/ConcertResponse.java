package io.hhplus.conbook.interfaces.api.concert;

import io.hhplus.conbook.domain.booking.BookingStatus;
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
            List<SeatDto> seats
    ) {}

    public record Booking (
            long bookingId,
            long concertId,
            LocalDate date,
            long seatId,
            BookingStatus status,
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
    public static class SeatDto {
        private long id;
        private String rowName;
        private int seatNo;
    }
}
