package io.hhplus.conbook.domain.token.generation;

import io.hhplus.conbook.domain.token.TokenStatus;

public enum TokenType {
    ACCESS, WAIT;

    public TokenStatus toItemStatus() {
        if (this.equals(ACCESS)) {
            return TokenStatus.PASSED;
        } else {
            return TokenStatus.WAITING;
        }
    }
}
