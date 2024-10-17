package io.hhplus.conbook.domain.token;

import java.util.List;

public interface TokenQueueRepository {

    /*
     * TokenQueue
     */
    void addQueue(TokenQueue tokenQueue);
    TokenQueue getTokenQueue(long concertId);
    List<TokenQueue> getQueueListWithoutItems();
    List<TokenQueue> getQueueListWithItems();
}
