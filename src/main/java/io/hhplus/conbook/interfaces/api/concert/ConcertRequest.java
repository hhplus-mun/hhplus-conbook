package io.hhplus.conbook.interfaces.api.concert;

public class ConcertRequest {
    public record Booking (
            String date,
            long seatId
    ) {}
}
