package io.hhplus.conbook.interfaces.api.token;

public class TokenResponse {
    public record Generate (
            String jwt
    ) {}

    public record Position (
            int queuePosition
    ) {}
}
