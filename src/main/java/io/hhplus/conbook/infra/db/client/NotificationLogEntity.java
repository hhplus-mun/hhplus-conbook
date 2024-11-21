package io.hhplus.conbook.infra.db.client;

import io.hhplus.conbook.domain.client.NotifactionLog;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Table(name = "notification_log")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class NotificationLogEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long bookingId;
    private LocalDateTime finishedAt;
    private LocalDateTime paidAt;

    public NotificationLogEntity(NotifactionLog log) {
        this.bookingId = log.getBookingId();
        this.finishedAt = log.getFinishedAt();
        this.paidAt = log.getPaidAt();
    }

    public NotifactionLog toDomain() {
        return new NotifactionLog(bookingId, finishedAt, paidAt);
    }
}
