package io.hhplus.conbook.domain.token;

import io.hhplus.conbook.domain.token.generation.CustomClaims;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class Token {
    private Integer id;
    private TokenQueue queue;
    private String userUUID;
    private TokenStatus status;
    private Integer position;
    private LocalDateTime createdAt;
    private LocalDateTime expiredAt;

    @Builder
    public Token(TokenQueue queue, String userUUID, TokenStatus status, Integer position, LocalDateTime createdAt, LocalDateTime expiredAt) {
        this.queue = queue;
        this.userUUID = userUUID;
        this.status = status;
        this.position = position;
        this.createdAt = createdAt;
        this.expiredAt = expiredAt;
    }

    public void switchStatusToPass() {
        status = TokenStatus.PASSED;
        expiredAt = LocalDateTime.now().plusMinutes(CustomClaims.EXPIRATION_MIN);
        position = -1;
    }

    public void changePosition(int position) {
        this.position = position;
    }

    public void expire() {
        this.expiredAt = LocalDateTime.now();
    }
}
