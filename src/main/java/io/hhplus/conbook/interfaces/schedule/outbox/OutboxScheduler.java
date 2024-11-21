package io.hhplus.conbook.interfaces.schedule.outbox;

import io.hhplus.conbook.config.KafkaConfig;
import io.hhplus.conbook.domain.outbox.OutboxEvent;
import io.hhplus.conbook.domain.outbox.OutboxEventStore;
import io.hhplus.conbook.domain.outbox.OutboxStatus;
import io.hhplus.conbook.infra.kafka.producer.KafkaBookingPaymentProducer;
import io.hhplus.conbook.infra.kafka.producer.KafkaConcertBookingProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxScheduler {

    private final OutboxEventStore outboxEventStore;
    private final KafkaConcertBookingProducer kafkaConcertBookingProducer;
    private final KafkaBookingPaymentProducer kafkaBookingPaymentProducer;

    @Scheduled(fixedDelay = 5000)
    public void sendingMessage() {
        List<OutboxEvent> failedEvents = outboxEventStore.findFailedList();

        if (failedEvents.isEmpty()) return;

        for (OutboxEvent failedEvent : failedEvents) {
            failedEvent.tryAgain();

            try {
                String message = failedEvent.getPayload();
                String topic = failedEvent.getEventType();

                if (topic.equals(KafkaConfig.TOPIC_CONCERT)) kafkaConcertBookingProducer.send(message);
                else if (topic.equals(KafkaConfig.TOPIC_BOOKING)) kafkaBookingPaymentProducer.send(message);

                failedEvent.changeStatus(OutboxStatus.PUBLISHED);
            } catch (Exception e) {
                if (failedEvent.getRetryCount() > 10) failedEvent.changeStatus(OutboxStatus.ABANDONED);
                log.error(e.getMessage());
            }
            outboxEventStore.save(failedEvent);
        }
    }
}
