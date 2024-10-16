package io.hhplus.conbook.domain.token.generation;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
class Payload {
    private long concertId;
    private String uuid;
    private LocalDateTime expiredAt;
    private Integer position;
    private TokenType tokenType;

    public Payload(TokenType tokenType, long concertId, String uuid) {
        this.tokenType = tokenType;
        this.concertId = concertId;
        this.uuid = uuid;
    }

    public void addPosition(int position) {
        this.position = position;
    }

    public void addExpiration(LocalDateTime expiredAt) {
        this.expiredAt = expiredAt;
    }
}

