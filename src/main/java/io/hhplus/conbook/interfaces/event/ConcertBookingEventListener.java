package io.hhplus.conbook.interfaces.event;

import io.hhplus.conbook.application.event.ConcertBookingEvent;
import io.hhplus.conbook.domain.outbox.OutboxEventStore;
import io.hhplus.conbook.domain.outbox.OutboxStatus;
import io.hhplus.conbook.infra.kafka.producer.KafkaConcertBookingProducer;
import io.hhplus.conbook.interfaces.schedule.booking.BookingScheduler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class ConcertBookingEventListener {

    private final BookingScheduler bookingScheduler;
    private final OutboxEventStore outboxEventStore;
    private final KafkaConcertBookingProducer kafkaConcertBookingProducer;

    @Order(1)
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handleForSchedule(ConcertBookingEvent event) {
        log.info("[SCHEDULE] HANDLE - {}", event);

        int DEFAULT_BOOKING_STAGING_MIN = 5;
        bookingScheduler.addSchedule(event.getBookingId(), DEFAULT_BOOKING_STAGING_MIN);
    }

    @Order(2)
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handleForOutbox(ConcertBookingEvent event) {
        log.info("[OUTBOX] HANDLE - {}", event);
        outboxEventStore.save(event);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleForMessaging(ConcertBookingEvent event) {
        log.info("[MESSAGING] HANDLE - {}", event);

        try {
            kafkaConcertBookingProducer.send(event);
            outboxEventStore.updateAs(event.getBookingId(), OutboxStatus.PUBLISHED);
        } catch (Exception e) {
            outboxEventStore.updateAs(event.getBookingId(), OutboxStatus.FAILED);
        }
    }
}
