package io.hhplus.conbook.interfaces.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.hhplus.conbook.application.client.ClientCommand;
import io.hhplus.conbook.application.client.ClientFacade;
import io.hhplus.conbook.application.client.ClientLogCommand;
import io.hhplus.conbook.application.client.ClientLogFacade;
import io.hhplus.conbook.application.event.ConcertBookingEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaConcertBookingConsumer {

    private ClientFacade clientFacade;
    private ClientLogFacade clientLogFacade;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "${kafka.topic.concert}")
    public void listen(String message) throws JsonProcessingException {
        log.info("[RECEIVED] message: {}", message);

        ClientCommand.Notify command
                = new ClientCommand.Notify(objectMapper.readValue(message, ConcertBookingEvent.class));

        clientFacade.notifyBookingHistory(command);
        clientLogFacade.saveNotificationHistory(new ClientLogCommand.Save(command.bookingId(), LocalDateTime.now()));
    }
}
