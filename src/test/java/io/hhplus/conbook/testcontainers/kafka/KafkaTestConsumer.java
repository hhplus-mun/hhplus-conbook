package io.hhplus.conbook.testcontainers.kafka;

import io.hhplus.conbook.config.KafkaConfig;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaTestConsumer {
    private String receivedMessage;

    public String getReceivedMessage() {
        return receivedMessage;
    }

    @KafkaListener(topics = KafkaConfig.TOPIC_CONCERT)
    public void listen(String message) {
        System.out.println("[RECEIVED]: " + message);
        receivedMessage = message;
    }
}
