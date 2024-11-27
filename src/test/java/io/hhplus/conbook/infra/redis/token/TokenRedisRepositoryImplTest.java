package io.hhplus.conbook.infra.redis.token;

import io.hhplus.conbook.domain.token.Token;
import io.hhplus.conbook.domain.token.TokenStatus;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class TokenRedisRepositoryImplTest {
    @Autowired
    TokenRedisRepositoryImpl tokenRedisRepository;

    @Test
    void tempTtt() {
        Token token = Token.builder()
                .status(TokenStatus.PASSED)
                .userUUID("ttt")
                .build();

        tokenRedisRepository.save(token);


        boolean existing = tokenRedisRepository.existsInPass(1L, "ttt");
        System.out.println("existing? = " + existing);
    }

    @Test
    @DisplayName("[정상]: redis position 조회 테스트")
    void findWaitingPosition() {
        // given
        long concertId = 1;
        String waitingToken = "test";

        // when
        int position = tokenRedisRepository.findPositionFor(concertId, waitingToken);
        System.out.println("position = " + position);

        assertThat(position).isGreaterThanOrEqualTo(0);
    }
}