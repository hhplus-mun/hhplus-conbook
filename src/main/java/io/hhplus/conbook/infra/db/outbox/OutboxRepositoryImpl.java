package io.hhplus.conbook.infra.db.outbox;

import io.hhplus.conbook.domain.outbox.OutboxEvent;
import io.hhplus.conbook.domain.outbox.OutboxRepository;
import io.hhplus.conbook.domain.outbox.OutboxStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class OutboxRepositoryImpl implements OutboxRepository {

    private final OutboxJpaRepository outboxJpaRepository;

    @Override
    public void save(OutboxEvent outboxEvent) {
        outboxJpaRepository.save(new OutboxEventEntity(outboxEvent));
    }

    @Override
    public List<OutboxEvent> findByStatus(OutboxStatus status) {
        return outboxJpaRepository.findByStatus(status)
                .stream()
                .map(OutboxEventEntity::toDomain)
                .toList();
    }

    @Override
    public OutboxEvent getByAggregateId(long aggregateId) {
        return outboxJpaRepository.findByAggregateId(aggregateId)
                .orElseThrow()
                .toDomain();
    }
}
