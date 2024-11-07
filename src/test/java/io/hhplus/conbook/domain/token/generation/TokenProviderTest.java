package io.hhplus.conbook.domain.token.generation;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TokenProviderTest {

    TokenProvider tokenProvider;

    public TokenProviderTest() {
        tokenProvider = new TokenProvider();
    }

    /*
     * Token 생성 테스트
     */

    @Test
    @DisplayName("[정상]: 서비스 이용권한 토큰 발급")
    void generateAccessToken() {
        // given
        long concertId = 1L;
        String userUUID = UUID.randomUUID().toString();
        CustomTokenClaims tokenClaims = CustomTokenClaims.getDefaultClaims(concertId, userUUID);

        // when
        String jwt = tokenProvider.generateToken(tokenClaims);
        System.out.println("JWT = " + jwt);

        // then
        Claims payload = Jwts.parser().verifyWith(tokenProvider.getSecretKey()).build().parseSignedClaims(jwt).getPayload();

        String concertIdByToken = (String) payload.get(CustomClaims.CONCERT);
        String uuid = (String) payload.get(CustomClaims.UUID);
        String tokenType = (String) payload.get(CustomClaims.TOKEN_TYPE);
        LocalDateTime expiration = payload.getExpiration().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

        assertThat(Long.parseLong(concertIdByToken)).isEqualTo(tokenClaims.getConcertId());
        assertThat(uuid).isEqualTo(tokenClaims.getUserUUID());
        assertThat(tokenType).isEqualTo(TokenType.ACCESS.toString());
        assertThat(expiration.truncatedTo(ChronoUnit.SECONDS)).isEqualTo(tokenClaims.getExpiredAt().truncatedTo(ChronoUnit.SECONDS));

        System.out.println("concertId = " + Long.parseLong(concertIdByToken));
        System.out.println("uuid = " + uuid);
        System.out.println("tokenType = " + TokenType.valueOf(tokenType));
        System.out.println("expiration = " + expiration);
    }

    @Test
    @DisplayName("[정상]: 대기열 토큰 발급")
    void generateWaitToken() {
        // given
        long concertId = 1L;
        String userUUID = UUID.randomUUID().toString();
        CustomTokenClaims tokenClaims = CustomTokenClaims.getDefaultClaims(concertId, userUUID);
        tokenClaims.changeType(TokenType.WAIT);

        // when
        String jwt = tokenProvider.generateToken(tokenClaims);
        System.out.println("JWT = " + jwt);

        // then
        Claims payload = Jwts.parser().verifyWith(tokenProvider.getSecretKey()).build().parseSignedClaims(jwt).getPayload();

        String concertIdByToken = (String) payload.get(CustomClaims.CONCERT);
        String uuid = (String) payload.get(CustomClaims.UUID);
        String tokenType = (String) payload.get(CustomClaims.TOKEN_TYPE);
        Date expiration = payload.getExpiration();

        assertThat(Long.parseLong(concertIdByToken)).isEqualTo(tokenClaims.getConcertId());
        assertThat(uuid).isEqualTo(tokenClaims.getUserUUID());
        assertThat(tokenType).isEqualTo(TokenType.WAIT.toString());
        assertThat(expiration).isNull();
    }
}