package io.hhplus.conbook.infra.db.token;

import io.hhplus.conbook.domain.token.TokenQueue;
import io.hhplus.conbook.domain.token.TokenQueueRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class TokenQueueRepositoryImpl implements TokenQueueRepository {
    @Override
    public void addQueue(TokenQueue tokenQueue) {
    }
    @Override
    public TokenQueue getTokenQueue(long concertId) {
        return null;
    }

    @Override
    public List<TokenQueue> getQueueListWithoutItems() {
        return new ArrayList<>();
    }

    @Override
    public List<TokenQueue> getQueueListWithItems() {
        return new ArrayList<>();
    }
}
