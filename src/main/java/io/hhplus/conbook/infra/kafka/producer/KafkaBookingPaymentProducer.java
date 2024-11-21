package io.hhplus.conbook.infra.kafka.producer;

import io.hhplus.conbook.config.KafkaConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaBookingPaymentProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;

    public void send(String message) {
        kafkaTemplate.send(KafkaConfig.TOPIC_BOOKING, message);
    }
}
