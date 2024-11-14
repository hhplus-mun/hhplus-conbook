package io.hhplus.conbook.domain.client;

public interface NotificationLogRepository {
    NotifactionLog save(NotifactionLog log);
    NotifactionLog findByBookingId(Long bookingId);
}