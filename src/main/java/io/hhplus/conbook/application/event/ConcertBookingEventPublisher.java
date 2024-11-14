package io.hhplus.conbook.application.event;

import io.hhplus.conbook.domain.booking.Booking;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ConcertBookingEventPublisher {
    private final ApplicationEventPublisher publisher;

    public void publishEventOn(Booking bookingResult) {
        log.info("EVENT - ConcertBookingEvent");

        publisher.publishEvent(new ConcertBookingEvent(bookingResult));
    }
}
