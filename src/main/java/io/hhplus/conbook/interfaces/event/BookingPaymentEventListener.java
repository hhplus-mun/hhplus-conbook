package io.hhplus.conbook.interfaces.event;

import io.hhplus.conbook.application.event.BookingPaymentEvent;
import io.hhplus.conbook.config.KafkaConfig;
import io.hhplus.conbook.domain.outbox.OutboxEvent;
import io.hhplus.conbook.domain.outbox.OutboxEventStore;
import io.hhplus.conbook.domain.outbox.OutboxStatus;
import io.hhplus.conbook.infra.kafka.producer.KafkaBookingPaymentProducer;
import io.hhplus.conbook.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookingPaymentEventListener {
    private final OutboxEventStore outboxEventStore;
    private final KafkaBookingPaymentProducer kafkaBookingPaymentProducer;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handleForOutbox(BookingPaymentEvent event) {
        log.info("[OUTBOX] HANDLE - {}", event);
        String payload = JsonUtil.toJson(event);

        outboxEventStore.save(new OutboxEvent(event.getPaymentId(), KafkaConfig.TOPIC_BOOKING, payload));
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleForMessaging(BookingPaymentEvent event) {
        log.info("[MESSAGING] HANDLE - {}", event);

        try {
            String message = JsonUtil.toJson(event);

            kafkaBookingPaymentProducer.send(message);
            outboxEventStore.updateAs(event.getBookingId(), OutboxStatus.PUBLISHED);
        } catch (Exception e) {
            outboxEventStore.updateAs(event.getBookingId(), OutboxStatus.FAILED);
        }
    }
}
