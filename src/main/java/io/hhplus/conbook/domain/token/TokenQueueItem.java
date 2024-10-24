package io.hhplus.conbook.domain.token;

import io.hhplus.conbook.domain.token.generation.CustomClaims;
import io.hhplus.conbook.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class TokenQueueItem {
    private Long id;
    private TokenQueue queue;
    private User user;
    private ItemStatus status;
    private Integer position;
    private LocalDateTime createdAt;
    private LocalDateTime expiredAt;

    @Builder
    public TokenQueueItem(TokenQueue queue, User user, ItemStatus status, Integer position, LocalDateTime createdAt, LocalDateTime expiredAt) {
        this.queue = queue;
        this.user = user;
        this.status = status;
        this.position = position;
        this.createdAt = createdAt;
        this.expiredAt = expiredAt;
    }

    public void switchStatusToPass() {
        status = ItemStatus.PASSED;
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
