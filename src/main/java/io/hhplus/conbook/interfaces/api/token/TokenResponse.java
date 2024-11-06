package io.hhplus.conbook.interfaces.api.token;

import io.hhplus.conbook.domain.token.generation.TokenType;

public class TokenResponse {
    public record Generate (
            String jwt,
            TokenType type
    ) {}

    public record Status (
            int position
    ) {}
}
