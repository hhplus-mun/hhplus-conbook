package io.hhplus.conbook.domain.token.generation;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Component
@Getter(AccessLevel.PACKAGE)
public class TokenProvider {

    private final SecretKey secretKey;

    public TokenProvider() {
        secretKey = Jwts.SIG.HS256.key().build();
    }

    /**
     * 대기순서 / 잔여시간
     * @return Json Web Token
     */
    public String generateToken(CustomTokenClaims tokenClaims) {
        Date issuedAt =
                Date.from(tokenClaims.getIssuedAt().atZone(ZoneId.systemDefault()).toInstant());

        return Jwts.builder()
                .issuer("conbook")
                .issuedAt(issuedAt)
                .claims(tokenClaims.getClaimsMap())
                .signWith(secretKey)
                .compact();
    }

    public boolean validate(String token) {
        try {
            // expiration 초과시 ExpiredJwtException 발생
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public AccessPayload extractAccess(String token) {
        Payload payload = parseFrom(token, TokenType.ACCESS);

        return new AccessPayload(payload.getConcertId(), payload.getUuid(), payload.getExpiredAt());
    }

    public WaitPayload extractWait(String token) {
        Payload payload = parseFrom(token, TokenType.WAIT);

        return new WaitPayload(payload.getConcertId(), payload.getUuid(), payload.getPosition());
    }

    private Payload parseFrom(String token, TokenType tokenType) {
        Claims claims = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
        TokenType parsedType = TokenType.valueOf((String) claims.get(CustomClaims.TOKEN_TYPE));
        if (!parsedType.equals(tokenType)) throw new IllegalArgumentException();

        String concertId = (String) claims.get(CustomClaims.CONCERT);
        String uuid = (String) claims.get(CustomClaims.UUID);
        Payload payload = new Payload(parsedType, Long.parseLong(concertId), uuid);

        if (parsedType.equals(TokenType.ACCESS)) {
            LocalDateTime expiredAt = claims.getExpiration().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            payload.addExpiration(expiredAt);
        }
        else {
            Integer position = (Integer) claims.get(CustomClaims.POSITION);
            payload.addPosition(position);
        }

        return payload;
    }
}
