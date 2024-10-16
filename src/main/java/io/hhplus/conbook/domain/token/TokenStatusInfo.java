package io.hhplus.conbook.domain.token;

import lombok.Getter;

/**
 * accessToken과 position은 동시에 초기화하면 안됨.
 */
@Getter
public class TokenStatusInfo {
    private String accessToken;
    private Integer position;
    private boolean isPassed;

    public TokenStatusInfo(String accessToken) {
        this.accessToken = accessToken;
        isPassed = true;
    }

    public TokenStatusInfo(Integer position) {
        this.position = position;
    }


}
