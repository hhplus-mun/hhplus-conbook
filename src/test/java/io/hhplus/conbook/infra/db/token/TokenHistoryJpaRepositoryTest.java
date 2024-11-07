package io.hhplus.conbook.infra.db.token;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TokenHistoryJpaRepositoryTest {
    @Autowired
    TokenHistoryJpaRepository tokenHistoryJpaRepository;

    @Test
    @DisplayName("[정상]: JPA 쿼리 확인 - 토큰 생성 히스토리 조회")
    void findTokenByConcertIdAndTokenValue() {
        tokenHistoryJpaRepository.findHistoryBy(1L, "uuid");
    }
}