package io.hhplus.conbook.config;

import io.hhplus.conbook.domain.token.ItemStatus;
import io.hhplus.conbook.domain.token.TokenManager;
import io.hhplus.conbook.domain.token.TokenQueue;
import io.hhplus.conbook.domain.token.TokenQueueItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class ScheduledTaskExecutor {
    private final TokenManager tokenManager;

    /**
     * scheduler interval time : 5min
     * > 5 * 60 * 1000 = 300,000
     */
    private static final long TOKEN_CHECKER_INTERVAL = 300000L;

    /**
     * 대기열 토큰 상태 업데이트
     */
    @Scheduled(fixedDelay = TOKEN_CHECKER_INTERVAL)
    public void updateQueueList() {
        for (TokenQueue queue : tokenManager.getTokenQueueListWithItems()) {
            Map<ItemStatus, List<TokenQueueItem>> tokenGroup = queue.getQueueItems().stream()
                    .collect(Collectors.groupingBy(TokenQueueItem::getStatus));

            List<TokenQueueItem> waitingItems = tokenGroup.getOrDefault(ItemStatus.WAITING, new ArrayList<>());
            List<TokenQueueItem> passedItems = tokenGroup.getOrDefault(ItemStatus.PASSED, new ArrayList<>());

            if (passedItems.size() == 0) continue;

            List<TokenQueueItem> expiredTokens = new ArrayList<>();
            for (TokenQueueItem passedItem : passedItems) {
                if (LocalDateTime.now().isAfter(passedItem.getExpiredAt())) {
                    expiredTokens.add(passedItem);
                }
            }

            tokenManager.removeExpiredAccessToken(expiredTokens);
            int availableAccess = queue.getAccessCapacity() - (passedItems.size() - expiredTokens.size());
            tokenManager.convertToPass(waitingItems, availableAccess);
        }
    }

}
