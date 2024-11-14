package io.hhplus.conbook.interfaces.schedule.booking;

import io.hhplus.conbook.domain.booking.BookingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
@Slf4j
@RequiredArgsConstructor
public class BookingScheduler {
    private final BookingService bookingService;
    private final TaskScheduler taskScheduler;

    public void addSchedule(long bookingId, long intervalMin) {
        // 단발성 스케쥴러 등록
        Runnable task = () -> {
            log.info("TaskScheduler has been executed for booking[{}]", bookingId);

            bookingService.checkOrUpdate(bookingId);
        };
        Instant startTime =
                LocalDateTime.now().plusMinutes(intervalMin).atZone(ZoneId.systemDefault()).toInstant();

        taskScheduler.schedule(task,startTime);
    }
}
