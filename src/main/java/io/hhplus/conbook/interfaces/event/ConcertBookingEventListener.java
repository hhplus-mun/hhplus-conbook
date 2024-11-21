package io.hhplus.conbook.interfaces.event;

import io.hhplus.conbook.application.client.ClientCommand;
import io.hhplus.conbook.application.event.ConcertBookingEvent;
import io.hhplus.conbook.infra.kafka.producer.KafkaConcertBookingProducer;
import io.hhplus.conbook.interfaces.schedule.booking.BookingScheduler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class ConcertBookingEventListener {

    private final BookingScheduler bookingScheduler;
    private final KafkaConcertBookingProducer kafkaConcertBookingProducer;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handleForSchedule(ConcertBookingEvent event) {
        log.info("[SCHEDULE] HANDLE - {}", event);

        int DEFAULT_BOOKING_STAGING_MIN = 5;
        bookingScheduler.addSchedule(event.getBooking().getId(), DEFAULT_BOOKING_STAGING_MIN);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleForMessaging(ConcertBookingEvent event) {
        log.info("[MESSAGING] HANDLE - {}", event);

        ClientCommand.Notify notification = new ClientCommand.Notify(event.getBooking());
        kafkaConcertBookingProducer.send(notification);
    }
}
