package io.hhplus.conbook.domain.token;

import java.util.List;

public interface TokenQueueItemRepository {
    List<TokenQueueItem> itemList(long concertId);
    void pushItem(TokenQueueItem tokenQueueItem);
    boolean existsInPass(long concertId, String userUUID);
    TokenQueueItem getItemBy(long concertId, String userUUID);
}
