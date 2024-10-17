package io.hhplus.conbook.infra.db.token;

import io.hhplus.conbook.domain.token.TokenQueueItem;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

@DataJpaTest
class TokenQueueItemJpaRepositoryTest {
    @Autowired
    TokenQueueItemJpaRepository itemJpaRepository;

    @Test
    @DisplayName("[정상]: JPA 쿼리 확인 - 토큰 큐 목록 조회")
    void findAllByTokenQueueId() {
        List<TokenQueueItemEntity> queItemList = itemJpaRepository.findAllByTokenQueueId(1L);

        List<TokenQueueItem> tokenQueueItemList = queItemList.stream()
                .map(TokenQueueItemEntity::toDomain)
                .toList();
    }

    @Test
    @DisplayName("[정상]: JPA 쿼리 확인 - 토큰 큐 항목 조회")
    void findItemByConcertIdAndUUID() {
        itemJpaRepository.findItemByConcertIdAndUUID(1L, "fdkfjakdsfs");
    }
}