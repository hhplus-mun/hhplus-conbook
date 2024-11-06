package io.hhplus.conbook.infra.db.token;

import io.hhplus.conbook.domain.token.TokenQueue;
import io.hhplus.conbook.domain.token.TokenQueueRepository;
import io.hhplus.conbook.interfaces.api.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class TokenQueueRepositoryImpl implements TokenQueueRepository {
    private final TokenQueueJpaRepository tokenQueueJpaRepository;

    @Override
    public void addQueue(TokenQueue tokenQueue) {
        tokenQueueJpaRepository.save(new TokenQueueEntity(tokenQueue));
    }

    @Override
    public TokenQueue getTokenQueue(long concertId) {
        return tokenQueueJpaRepository.findByConcertId(concertId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.QUEUE_NOT_FOUND.getCode()))
                .toDomainWithoutTokens();
    }

    @Transactional
    @Override
    public List<TokenQueue> getQueueListWithoutTokens() {
        return tokenQueueJpaRepository.findAll().stream()
                .map(TokenQueueEntity::toDomainWithoutTokens)
                .toList();
    }

    @Override
    public List<TokenQueue> getQueueListWithTokens() {
        return tokenQueueJpaRepository.findAllWithTokens().stream()
                .map(TokenQueueEntity::toDomainWithTokens)
                .toList();
    }
}
