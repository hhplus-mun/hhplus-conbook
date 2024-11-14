package io.hhplus.conbook.infra.db.client;

import io.hhplus.conbook.domain.client.NotifactionLog;
import io.hhplus.conbook.domain.client.NotificationLogRepository;
import io.hhplus.conbook.interfaces.api.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class NotificationLogRepositoryImpl implements NotificationLogRepository {

    private final NotificationLogJpaRepository notificationLogJpaRepository;

    @Override
    public NotifactionLog save(NotifactionLog log) {
        return notificationLogJpaRepository.save(new NotificationLogEntity(log)).toDomain();
    }

    @Override
    public NotifactionLog findByBookingId(Long bookingId) {
        return notificationLogJpaRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.INTERNAL_SERVER_ERROR.getCode()))
                .toDomain();
    }
}
