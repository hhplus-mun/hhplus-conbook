package io.hhplus.conbook.domain.outbox;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@Getter
public class OutboxEvent {
    private long id;
    private long aggregateId;
    /** topic */
    private String eventType;
    private String payload;
    private OutboxStatus status;
    private LocalDateTime createdAt;
    private int retryCount;

    public OutboxEvent(long aggregateId, String eventType, String payload) {
        this.aggregateId = aggregateId;
        this.eventType = eventType;
        this.payload = payload;
        status = OutboxStatus.CREATED;
        createdAt = LocalDateTime.now();
    }

    public void changeStatus(OutboxStatus status) {
        this.status = status;
    }
}
