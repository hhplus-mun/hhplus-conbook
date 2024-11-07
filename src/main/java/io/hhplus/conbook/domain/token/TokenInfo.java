package io.hhplus.conbook.domain.token;

import io.hhplus.conbook.domain.token.generation.TokenType;

public record TokenInfo(
        String jwt,
        TokenType type
) {}
