package io.hhplus.conbook.testcontainers.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaTestConsumer {
    private String receivedMessage;

    public String getReceivedMessage() {
        return receivedMessage;
    }

    @KafkaListener(topics = "${kafka.topic.concert}")
    public void listen(String message) {
        System.out.println("[RECEIVED]: " + message);
        receivedMessage = message;
    }
}
