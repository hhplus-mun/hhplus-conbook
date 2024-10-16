package io.hhplus.conbook.application.token;

public class TokenCommand {
    public record Access (
            long userId,
            long concertId
    ) {}

    public record Check (
            String waitingToken
    ) {}
}
