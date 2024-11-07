package io.hhplus.conbook.domain.token;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@ToString
public class TokenStatusCount {
    private TokenStatus status;
    private long count;

    public TokenStatusCount(TokenStatus status, long count) {
        this.status = status;
        this.count = count;
    }
}
