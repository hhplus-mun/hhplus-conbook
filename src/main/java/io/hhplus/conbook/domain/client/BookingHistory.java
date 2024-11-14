package io.hhplus.conbook.domain.client;

import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
public record BookingHistory(
        Long bookingId,
        Long concertId,
        String title,
        LocalDate concertDate,
        Long seatId,
        String seatRow,
        int seatNo,
        Long userId,
        String userName,
        LocalDateTime bookingDateTime) {
}
