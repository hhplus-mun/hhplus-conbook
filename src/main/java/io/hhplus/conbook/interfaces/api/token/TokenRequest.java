package io.hhplus.conbook.interfaces.api.token;

public class TokenRequest {
    public record Generate (
            long userId,
            long concertId
    ) {}
}
