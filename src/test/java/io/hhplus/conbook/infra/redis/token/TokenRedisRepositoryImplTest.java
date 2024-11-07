package io.hhplus.conbook.infra.redis.token;

import io.hhplus.conbook.domain.token.Token;
import io.hhplus.conbook.domain.token.TokenStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
}