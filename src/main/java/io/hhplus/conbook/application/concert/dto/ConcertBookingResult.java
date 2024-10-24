package io.hhplus.conbook.application.concert.dto;

import lombok.Builder;

import java.time.LocalDateTime;

public class ConcertBookingResult {
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
