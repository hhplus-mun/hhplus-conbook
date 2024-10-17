package io.hhplus.conbook.application.booking;

public class BookingCommand {
    public record Paid(
            long bookingId,
            String userUUID
    ) {}
}
