package io.hhplus.conbook.infra.kafka.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.hhplus.conbook.application.client.ClientCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaConcertBookingProducer {

    @Value("${kafka.topic.concert}")
    private String concertTopic;

    private final KafkaTemplate<String, String> kafkaTemplate;

    private final ObjectMapper mapper = new ObjectMapper();

    public void send(ClientCommand.Notify notification){
        String message;
        try {
            message = mapper.writeValueAsString(notification);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        kafkaTemplate.send(concertTopic, message);
    }
}
