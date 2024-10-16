package io.hhplus.conbook.domain.token.generation;

import io.hhplus.conbook.domain.token.ItemStatus;

public enum TokenType {
    ACCESS, WAIT;

    public ItemStatus toItemStatus() {
        if (this.equals(ACCESS)) {
            return ItemStatus.PASSED;
        } else {
            return ItemStatus.WAITING;
        }
    }
}
