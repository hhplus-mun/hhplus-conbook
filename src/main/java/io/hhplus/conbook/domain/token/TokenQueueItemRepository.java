package io.hhplus.conbook.domain.token;

import java.util.List;

public interface TokenQueueItemRepository {
    List<TokenQueueItem> itemList(long concertId);
    void pushItem(TokenQueueItem tokenQueueItem);
    boolean existsInPass(long concertId, String userUUID);
    TokenQueueItem findItemBy(long concertId, String userUUID);

    void updateStatus(TokenQueueItem tokenQueueItem);

    void remove(TokenQueueItem tokenItem);
}
