package io.hhplus.conbook.config;

import io.hhplus.conbook.domain.booking.BookingService;
import io.hhplus.conbook.domain.concert.ConcertService;
import io.hhplus.conbook.domain.token.TokenManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
@Slf4j
@RequiredArgsConstructor
public class ScheduledTaskExecutor {
    private final TokenManager tokenManager;
    private final BookingService bookingService;
    private final ConcertService concertService;
    private final TaskScheduler taskScheduler;

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

    public void addSchedule(long bookingId, long intervalMin) {
        // 단발성 스케쥴러 등록
        Runnable task = () -> {
            log.info("\nTaskScheduler has been executed");

            bookingService.checkOrUpdate(bookingId);
        };
        Instant startTime =
                LocalDateTime.now().plusMinutes(intervalMin).atZone(ZoneId.systemDefault()).toInstant();

        taskScheduler.schedule(task,startTime);
    }
}
