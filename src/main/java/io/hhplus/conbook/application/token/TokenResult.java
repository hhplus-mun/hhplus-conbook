package io.hhplus.conbook.application.token;

import io.hhplus.conbook.domain.token.TokenStatusInfo;
import io.hhplus.conbook.domain.token.generation.TokenType;

public class TokenResult {
    public record Access(
            String jwt,
            TokenType type
    ) {}

    public record Check(
            TokenStatusInfo tokenStatusInfo
    ) {}

}
