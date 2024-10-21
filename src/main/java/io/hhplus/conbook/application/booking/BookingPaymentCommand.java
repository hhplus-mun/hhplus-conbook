package io.hhplus.conbook.application.booking;

import lombok.Builder;

import java.time.LocalDateTime;

public class BookingPaymentCommand {
    @Builder
    public record Paid(
            long bookingId,
            long concertId,
            String userUUID,
            LocalDateTime reqTime
    ) {}
}
