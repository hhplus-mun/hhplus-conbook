package io.hhplus.conbook.infra.db.token;

import io.hhplus.conbook.domain.token.Token;
import io.hhplus.conbook.domain.token.TokenStatusCount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class TokenJpaRepositoryTest {
    @Autowired
    TokenJpaRepository tokenJpaRepository;

    @BeforeEach
    void init() {
        System.out.println("\n[START]: jpa query");
    }

    @Test
    @DisplayName("[정상]: JPA 쿼리 확인 - 토큰 목록 조회")
    void findAllByTokenQueueId() {
        List<TokenEntity> tokenEntityList = tokenJpaRepository.findAllByTokenQueueId(1L);

        List<Token> tokenList = tokenEntityList.stream()
                .map(TokenEntity::toDomain)
                .toList();
    }

    @Test
    @DisplayName("[정상]: JPA 쿼리 확인 - 토큰 조회")
    void findTokenByConcertIdAndUUID() {
        tokenJpaRepository.findTokenByConcertIdAndUUID(1L, "fdkfjakdsfs");
    }

    @Test
    @DisplayName("[정상]: JPA 쿼리 확인 - 토큰 조회")
    void findTokenByQueueIdAndUUID() {
        tokenJpaRepository.findTokenByQueueIdAndUUID(1L, "fdkfjakdsfs");
    }

    @Test
    @DisplayName("[정상]: JPA 쿼리 확인 - 상태에 따른 토큰 수량 조회")
    void findTokenStatusCount() {
        List<TokenStatusCount> tokenStatusCounts = tokenJpaRepository.findTokenStatusCountBy(1L);

        for (TokenStatusCount tokenStatusCount : tokenStatusCounts) {
            System.out.println("tokenStatusCount = " + tokenStatusCount);
        }
    }
}