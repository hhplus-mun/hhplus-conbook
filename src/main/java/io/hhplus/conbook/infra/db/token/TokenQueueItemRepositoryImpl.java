package io.hhplus.conbook.infra.db.token;

import io.hhplus.conbook.domain.token.ItemStatus;
import io.hhplus.conbook.domain.token.TokenQueueItem;
import io.hhplus.conbook.domain.token.TokenQueueItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class TokenQueueItemRepositoryImpl implements TokenQueueItemRepository {

    private final TokenQueueJpaRepository tokenQueueJpaRepository;
    private final TokenQueueItemJpaRepository tokenQueueItemJpaRepository;

    @Override
    public List<TokenQueueItem> itemList(long concertId) {
        TokenQueueEntity tokenQueue = tokenQueueJpaRepository.findByConcertId(concertId)
                .orElseThrow(() -> new IllegalArgumentException());

        return tokenQueueItemJpaRepository.findAllByTokenQueueId(tokenQueue.getId())
                .stream()
                .map(TokenQueueItemEntity::toDomain)
                .toList();
    }

    @Override
    public void pushItem(TokenQueueItem tokenQueueItem) {
        tokenQueueItemJpaRepository.save(new TokenQueueItemEntity(tokenQueueItem));
    }

    @Override
    public TokenQueueItem findItemBy(long concertId, String userUUID) {

        return tokenQueueItemJpaRepository.findItemByConcertIdAndUUID(concertId, userUUID)
                .orElseThrow(() -> new IllegalArgumentException())
                .toDomain();
    }

    @Override
    public boolean existsInPass(long concertId, String userUUID) {
        TokenQueueItemEntity item =
                tokenQueueItemJpaRepository.findItemByConcertIdAndUUID(concertId, userUUID)
                .orElseThrow(() -> new IllegalArgumentException());

        return item.getStatus().equals(ItemStatus.PASSED) ? true : false;
    }

    @Override
    public void updateStatus(TokenQueueItem tokenQueueItem) {
        tokenQueueItemJpaRepository.save(new TokenQueueItemEntity(tokenQueueItem));
    }

    @Override
    public void remove(TokenQueueItem item) {
        tokenQueueItemJpaRepository.delete(new TokenQueueItemEntity(item));
    }

    @Override
    public void saveOrUpdate(TokenQueueItem tokenQueueItem) {
        tokenQueueItemJpaRepository.save(new TokenQueueItemEntity(tokenQueueItem));
    }
}
