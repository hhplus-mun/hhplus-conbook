package io.hhplus.conbook.interfaces.schedule.token;

import io.hhplus.conbook.domain.concert.ConcertService;
import io.hhplus.conbook.domain.token.TokenManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenScheduler {
    private final TokenManager tokenManager;
    private final ConcertService concertService;

    /**
     * scheduler interval time : 5min
     * > 5 * 60 * 1000 = 300,000
     */
    public static final long TOKEN_CHECKER_INTERVAL = 300000L;

    /**
     * 대기열 토큰 상태 업데이트
     */
    @Scheduled(fixedDelay = TOKEN_CHECKER_INTERVAL)
    public void updateQueueList() {
        for (Long concertId : concertService.getConcertIds()) {
            tokenManager.clearNonValidTokens(concertId);
            tokenManager.convertToPass(concertId);
        }
    }
}
