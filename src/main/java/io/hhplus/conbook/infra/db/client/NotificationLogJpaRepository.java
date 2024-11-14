package io.hhplus.conbook.infra.db.client;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NotificationLogJpaRepository extends JpaRepository<NotificationLogEntity, Long> {
    Optional<NotificationLogEntity> findByBookingId(Long bookingId);
}
