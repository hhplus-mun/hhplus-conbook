package io.hhplus.conbook.domain.outbox;

import java.util.List;

public interface OutboxRepository {
    void save(OutboxEvent outboxEvent);
    List<OutboxEvent> findByStatus(OutboxStatus status);
    OutboxEvent getByAggregateId(long aggregateId);
}
