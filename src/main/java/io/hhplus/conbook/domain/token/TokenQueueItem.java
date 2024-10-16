package io.hhplus.conbook.domain.token;

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
    private int position;
    private LocalDateTime createdAt;
    private LocalDateTime expiredAt;

    @Builder
    public TokenQueueItem(TokenQueue queue, User user, ItemStatus status, int position, LocalDateTime createdAt, LocalDateTime expiredAt) {
        this.queue = queue;
        this.user = user;
        this.status = status;
        this.position = position;
        this.createdAt = createdAt;
        this.expiredAt = expiredAt;
    }
}
