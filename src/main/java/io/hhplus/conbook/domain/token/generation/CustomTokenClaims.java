package io.hhplus.conbook.domain.token.generation;

import io.jsonwebtoken.Claims;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Getter
public class CustomTokenClaims implements CustomClaims {
    private long concertId;
    private String userUUID;
    private TokenType type;
    private LocalDateTime issuedAt;
    private LocalDateTime expiredAt;

    /**
     * 기본 발급 토큰 유형은 Access 토큰이다.
     *
     * @param concertId
     * @param userUUID
     * @return AccessToken
     */
    public static CustomTokenClaims getDefaultClaims(long concertId, String userUUID) {
        return new CustomTokenClaims(concertId, userUUID);
    }

    public CustomTokenClaims(long concertId, String userUUID) {
        this.concertId = concertId;
        this.userUUID = userUUID;
        this.issuedAt = LocalDateTime.now();
        this.type = TokenType.ACCESS;
    }

    public void changeType(TokenType type) {
        this.type = type;
    }

    public Map<String, Object> getClaimsMap() {
        Map<String, Object> claimsMap = new HashMap<>();
        claimsMap.put(CONCERT, String.valueOf(concertId));
        claimsMap.put(UUID, userUUID);
        claimsMap.put(TOKEN_TYPE, type);

        if (type.equals(TokenType.WAIT)) {
            expiredAt = issuedAt.plusMinutes(WAITING_EXPIRATION_MIN);
        }
        else {
            expiredAt = issuedAt.plusMinutes(ACCESS_EXPIRATION_MIN);
        }
        Date expiration = Date.from(expiredAt.atZone(ZoneId.systemDefault()).toInstant());
        claimsMap.put(Claims.EXPIRATION, expiration);

        return claimsMap;
    }
}
