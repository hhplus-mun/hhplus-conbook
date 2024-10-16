package io.hhplus.conbook.domain.token.generation;

import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
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
        // expiration 초과시 ExpiredJwtException 발생
        Date issuedAt =
                Date.from(tokenClaims.getIssuedAt().atZone(ZoneId.systemDefault()).toInstant());

        return Jwts.builder()
                .issuer("conbook")
                .issuedAt(issuedAt)
                .claims(tokenClaims.getClaimsMap())
                .signWith(secretKey)
                .compact();
    }

}
