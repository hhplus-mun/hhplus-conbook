package io.hhplus.conbook.testcontainers.kafka;

import io.hhplus.conbook.config.KafkaConfig;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaTestProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaTestProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void produce(String message) {
        kafkaTemplate.send(KafkaConfig.TOPIC_CONCERT, message);
    }

}
