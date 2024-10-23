package io.hhplus.conbook.domain.token.generation;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Slf4j
@Component
@Getter(AccessLevel.PACKAGE)
public class TokenProvider {

    private final SecretKey secretKey;

    /**
     * key 생성에 사용되는 String 값은 hhplus 과제이기 때문에 노출된다. <br>
     * 실제 프로젝트에서 사용 시 외부에 노출시키지 않는다.
     * 
     * 알고리즘: 32byte(256bits)이기 때문에 HmacSHA256 사용
     */
    public TokenProvider() {
        String plainKey = "HANGHAEPLUS_TOKEN_SECRET_KEY_2024_BACKEND_6TH";
        secretKey = createSecretKey(plainKey);
    }

    private SecretKey createSecretKey(String plainKey) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] keyBytes = md.digest(plainKey.getBytes(StandardCharsets.UTF_8));
            return Keys.hmacShaKeyFor(keyBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 Algorithm을 찾을 수 없습니다.");
        }
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
