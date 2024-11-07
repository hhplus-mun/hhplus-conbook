package io.hhplus.conbook.infra.db.token;

import io.hhplus.conbook.domain.token.TokenHistory;
import io.hhplus.conbook.domain.token.generation.TokenType;
import io.hhplus.conbook.infra.db.concert.ConcertEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Table(name = "token_history")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class TokenHistoryEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "token_history_id")
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concert_id")
    private ConcertEntity concert;
    @Column(name = "user_uuid", nullable = false)
    private String userUUID;
    @Enumerated(EnumType.STRING)
    private TokenType tokenType;
    @Column(updatable = false)
    private LocalDateTime issuedAt;
    private LocalDateTime expiresAt;

    public TokenHistoryEntity(TokenHistory history) {
        this.id = history.getId();
        this.concert = new ConcertEntity(history.getConcert());
        this.userUUID = history.getUserUUID();
        this.tokenType = history.getTokenType();
        this.issuedAt = history.getIssuedAt();
        this.expiresAt = history.getExpiresAt();
    }

    public TokenHistory toDomain() {
        return new TokenHistory(id, concert.toDomain(), userUUID, tokenType, issuedAt, expiresAt);
    }
}
