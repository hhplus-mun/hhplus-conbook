package io.hhplus.conbook.domain.token;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class Token {
    private Integer id;
    private Long concertId;
    private String userUUID;
    private TokenStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime expiredAt;
    private String tokenValue;

    @Builder
    public Token(Long concertId, String userUUID, TokenStatus status, LocalDateTime createdAt, LocalDateTime expiredAt) {
        this.concertId = concertId;
        this.userUUID = userUUID;
        this.status = status;
        this.createdAt = createdAt;
        this.expiredAt = expiredAt;
    }

    public void issuedAs(String jwt) {
        this.tokenValue = jwt;
    }

    public void expire() {
        this.expiredAt = LocalDateTime.now();
    }
}
