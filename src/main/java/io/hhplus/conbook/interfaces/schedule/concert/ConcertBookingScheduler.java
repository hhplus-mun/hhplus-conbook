package io.hhplus.conbook.interfaces.schedule.concert;

import io.hhplus.conbook.domain.outbox.OutboxEvent;
import io.hhplus.conbook.domain.outbox.OutboxEventStore;
import io.hhplus.conbook.domain.outbox.OutboxStatus;
import io.hhplus.conbook.infra.kafka.producer.KafkaConcertBookingProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ConcertBookingScheduler {

    private final OutboxEventStore outboxEventStore;
    private final KafkaConcertBookingProducer kafkaConcertBookingProducer;

    @Scheduled(fixedDelay = 5000)
    public void sendingMessage() {
        List<OutboxEvent> failedEvents = outboxEventStore.findFailedList();

        if (failedEvents.isEmpty()) return;

        for (OutboxEvent failedEvent : failedEvents) {
            failedEvent.tryAgain();

            try {
                String message = failedEvent.getPayload();
                kafkaConcertBookingProducer.send(message);
                failedEvent.changeStatus(OutboxStatus.PUBLISHED);
            } catch (Exception e) {
                if (failedEvent.getRetryCount() > 10) failedEvent.changeStatus(OutboxStatus.ABANDONED);
                log.error(e.getMessage());
            }
            outboxEventStore.save(failedEvent);
        }
    }
}
