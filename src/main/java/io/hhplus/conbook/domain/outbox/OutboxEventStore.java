package io.hhplus.conbook.domain.outbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.hhplus.conbook.application.event.ConcertBookingEvent;
import io.hhplus.conbook.config.KafkaConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OutboxEventStore {

    private OutboxRepository outboxRepository;
    /**
     * saveOrUpdate (merge)
     */
    @Transactional
    public void save(OutboxEvent event) {
        outboxRepository.save(event);
    }

    public List<OutboxEvent> findFailedList() {
        return outboxRepository.findByStatus(OutboxStatus.FAILED);
    }

    @Transactional
    public void updateAs(long aggregateId, OutboxStatus status) {
        OutboxEvent event = outboxRepository.getByAggregateId(aggregateId);

        event.changeStatus(status);
        if (status.equals(OutboxStatus.FAILED)) event.tryAgain();

        outboxRepository.save(event);
    }
}
