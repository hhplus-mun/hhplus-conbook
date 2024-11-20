package io.hhplus.conbook.testcontainers.kafka;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaTestProducer {

    @Value("${kafka.topic.concert}")
    private String concertTopic;

    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaTestProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void produce(String message) {
        kafkaTemplate.send(concertTopic, message);
    }

}
