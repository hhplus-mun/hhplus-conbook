package io.hhplus.conbook.interfaces.api.token;

import io.hhplus.conbook.domain.token.ItemStatus;
import io.hhplus.conbook.domain.token.TokenStatusInfo;
import io.hhplus.conbook.domain.token.generation.TokenType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

public class TokenResponse {
    public record Generate (
            String jwt,
            TokenType type
    ) {}

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Status {
        private ItemStatus status;
        private String meesage;
        private Integer position;
        private String accessToken;

        public Status(TokenStatusInfo statusInfo) {
            this();

            if (statusInfo.isPassed()) {
                status = ItemStatus.PASSED;
                accessToken = statusInfo.getAccessToken();
                meesage = "대기열 통과";

            } else  {
                status = ItemStatus.WAITING;
                position = statusInfo.getPosition();
                meesage = String.format("현재 대기 중입니다. 대기번호: %d", position);
            }
        }
    }
}
