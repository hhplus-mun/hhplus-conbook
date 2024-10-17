package io.hhplus.conbook.application.concert;

public class ConcertCommand {
    public record Search(
            long concertId
    ) {}

    public record Status(
            long concertId,
            String date
    ) {}

    public record Booking(
            long concertId,
            String date,
            String userUUID,
            long seatId
    ){}
}
