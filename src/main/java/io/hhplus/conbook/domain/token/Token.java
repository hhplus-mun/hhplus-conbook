package io.hhplus.conbook.domain.token;

import io.hhplus.conbook.domain.token.generation.TokenType;

public record Token (
        String jwt,
        TokenType type
) {}
