package io.hhplus.conbook.application.concert;

import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class ConcertResult {
    @Builder
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

    @Builder
    public record SeatInfo (
            long id,
            String rowName,
            int seatNo
    ) {}

    @Builder
    public record BookingSeat(
            long bookingId,
            String userName,
            String rowName,
            int seatNo,
            LocalDateTime bookingDateTime,
            LocalDateTime expirationTime
    ) {}
}
