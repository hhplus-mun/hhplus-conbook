package io.hhplus.conbook.interfaces.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.hhplus.conbook.application.client.ClientFacade;
import io.hhplus.conbook.application.event.BookingPaymentEvent;
import io.hhplus.conbook.config.KafkaConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaBookingPaymentsConsumer {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private ClientFacade clientFacade;

    @KafkaListener(topics = KafkaConfig.TOPIC_BOOKING)
    public void listen(String message) throws JsonProcessingException {
        log.info("[RECEIVED] message: {}", message);

        BookingPaymentEvent bookingPaymentEvent = objectMapper.readValue(message, BookingPaymentEvent.class);
    }
}
