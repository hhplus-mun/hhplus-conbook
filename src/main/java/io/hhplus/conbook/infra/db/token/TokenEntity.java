package io.hhplus.conbook.infra.db.token;

import io.hhplus.conbook.domain.token.TokenStatus;
import io.hhplus.conbook.domain.token.Token;
import io.hhplus.conbook.infra.db.user.UserEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Table(name = "token")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class TokenEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "token_queue_id")
    private TokenQueueEntity tokenQueue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Enumerated(EnumType.STRING)
    private TokenStatus status;

    private Integer position;

    @Column(updatable = false)
    private LocalDateTime createdAt;
    private LocalDateTime expiredAt;

    public Token toDomain() {
        return new Token(
                id,
                tokenQueue.toDomainWithoutTokens(),
                user.toDomain(),
                status,
                position,
                createdAt,
                expiredAt
        );
    }

    public TokenEntity(Token token) {
        this.id = token.getId();
        this.status = token.getStatus();
        this.position = token.getPosition();
        this.createdAt = token.getCreatedAt();
        this.expiredAt = token.getExpiredAt();

        this.tokenQueue = new TokenQueueEntity(token.getQueue());
        this.user = new UserEntity(token.getUser());
    }
}
