package io.hhplus.conbook.domain.token;

import io.hhplus.conbook.domain.concert.Concert;
import io.hhplus.conbook.domain.token.generation.CustomTokenClaims;
import io.hhplus.conbook.domain.token.generation.TokenType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

import static io.hhplus.conbook.domain.token.generation.CustomClaims.EXPIRATION_MIN;

@AllArgsConstructor
@Getter
public class TokenHistory {
    private long id;
    private Concert concert;
    private String userUUID;
    private TokenType tokenType;
    private LocalDateTime issuedAt;
    private LocalDateTime expiresAt;

    public TokenHistory(Concert concert, String userUUID, TokenType tokenType) {
        this.concert = concert;
        this.userUUID = userUUID;
        this.tokenType = tokenType;
        issuedAt = LocalDateTime.now();
        if (TokenType.ACCESS.equals(tokenType))
            expiresAt = LocalDateTime.now().plusMinutes(EXPIRATION_MIN);
    }
}
