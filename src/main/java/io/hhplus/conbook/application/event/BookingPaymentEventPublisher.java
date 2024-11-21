package io.hhplus.conbook.application.event;

import io.hhplus.conbook.domain.payment.Payment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookingPaymentEventPublisher {
    private final ApplicationEventPublisher publisher;

    public void publishEventOn(Payment payment) {
        log.info("EVENT - BookingPaymentEvent");

        publisher.publishEvent(new BookingPaymentEvent(payment));
    }
}
