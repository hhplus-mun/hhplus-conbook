package io.hhplus.conbook.domain.outbox;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@Getter @Setter
public class OutboxEvent {
    private long id;
    /** topic */
    private String eventType;
    private String payload;
    private OutboxStatus status;
    private LocalDateTime createdAt;
    private int retryCount;

    public OutboxEvent(String eventType, String payload) {
        this.eventType = eventType;
        this.payload = payload;
        status = OutboxStatus.CREATED;
        createdAt = LocalDateTime.now();
    }
}
