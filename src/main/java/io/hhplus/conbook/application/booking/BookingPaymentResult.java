package io.hhplus.conbook.application.booking;

import java.time.LocalDateTime;

public class BookingPaymentResult {
    public record Paid(
            long bookingId,
            long paymentPrice,
            LocalDateTime paymentTime
    ) {}
}
