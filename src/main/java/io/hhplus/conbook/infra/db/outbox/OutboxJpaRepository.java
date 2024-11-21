package io.hhplus.conbook.infra.db.outbox;

import io.hhplus.conbook.domain.outbox.OutboxStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OutboxJpaRepository extends JpaRepository<OutboxEventEntity, Long> {
    List<OutboxEventEntity> findByStatus(OutboxStatus status);

    Optional<OutboxEventEntity> findByAggregateId(Long aggregateId);
}
