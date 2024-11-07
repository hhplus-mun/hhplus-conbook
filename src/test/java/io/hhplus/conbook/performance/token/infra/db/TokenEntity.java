package io.hhplus.conbook.performance.token.infra.db;

import io.hhplus.conbook.domain.token.Token;
import io.hhplus.conbook.domain.token.TokenStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Table(name = "token")
@Entity
public class TokenEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Long concertId;
    @Column(name = "user_uuid")
    private String userUUID;

    @Enumerated(EnumType.STRING)
    private TokenStatus status;

    @Column(updatable = false)
    private LocalDateTime createdAt;
    private LocalDateTime expiredAt;

    private String tokenValue;

    public TokenEntity() {}

    public TokenEntity(Token token) {
        this.id = token.getId();
        this.concertId = token.getConcertId();
        this.status = token.getStatus();
        this.createdAt = token.getCreatedAt();
        this.expiredAt = token.getExpiredAt();
        this.userUUID = token.getUserUUID();
        this.tokenValue = token.getTokenValue();
    }

    public Token toDomain() {
        return new Token(
                id,
                concertId,
                userUUID,
                status,
                createdAt,
                expiredAt,
                tokenValue
        );
    }

    public TokenStatus getStatus() {
        return status;
    }
}
