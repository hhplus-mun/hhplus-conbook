package io.hhplus.conbook.infra.db.outbox;

import io.hhplus.conbook.domain.outbox.OutboxEvent;
import io.hhplus.conbook.domain.outbox.OutboxStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Table(name = "outbox_event")
@Entity
@Getter
@NoArgsConstructor
public class OutboxEventEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    /** topic */
    private String eventType;

    @Column(columnDefinition = "TEXT")
    private String payload;

    @Enumerated(EnumType.STRING)
    private OutboxStatus status;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private int retryCount;

    public OutboxEvent toDomain() {
        return OutboxEvent.builder()
                .id(id)
                .eventType(eventType)
                .payload(payload)
                .status(status)
                .createdAt(createdAt)
                .retryCount(retryCount)
                .build();
    }

    public OutboxEventEntity(OutboxEvent event) {
        this.id = event.getId();
        this.eventType = event.getEventType();
        this.payload = event.getPayload();
        this.status = event.getStatus();
        this.createdAt = event.getCreatedAt();
        this.retryCount = event.getRetryCount();
    }
}
