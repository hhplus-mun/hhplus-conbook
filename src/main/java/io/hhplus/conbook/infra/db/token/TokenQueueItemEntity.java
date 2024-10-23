package io.hhplus.conbook.infra.db.token;

import io.hhplus.conbook.domain.token.ItemStatus;
import io.hhplus.conbook.domain.token.TokenQueueItem;
import io.hhplus.conbook.infra.db.user.UserEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Table(name = "token_queue_item")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class TokenQueueItemEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "token_queue_id")
    private TokenQueueEntity tokenQueue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Enumerated(EnumType.STRING)
    private ItemStatus status;

    private Integer position;

    @Column(updatable = false)
    private LocalDateTime createdAt;
    private LocalDateTime expiredAt;

    public TokenQueueItem toDomain() {
        return new TokenQueueItem(
                id,
                tokenQueue.toDomainWithoutItems(),
                user.toDomain(),
                status,
                position,
                createdAt,
                expiredAt
        );
    }

    public TokenQueueItemEntity(TokenQueueItem tokenQueueItem) {
        this.id = tokenQueueItem.getId();
        this.status = tokenQueueItem.getStatus();
        this.position = tokenQueueItem.getPosition();
        this.createdAt = tokenQueueItem.getCreatedAt();
        this.expiredAt = tokenQueueItem.getExpiredAt();


        this.tokenQueue = new TokenQueueEntity(tokenQueueItem.getQueue());
        this.user = new UserEntity(tokenQueueItem.getUser());
    }
}
