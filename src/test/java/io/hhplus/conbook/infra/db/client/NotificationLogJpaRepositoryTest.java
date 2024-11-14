package io.hhplus.conbook.infra.db.client;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class NotificationLogJpaRepositoryTest {
    @Autowired
    NotificationLogJpaRepository notificationLogJpaRepository;

    @Test
    @DisplayName("[정상]: JPA 쿼리 확인 - 저장된 알림 로그 확인")
    void queryForNotifaciotn() {
        // given
        long bookindId = 1;

        // when
        notificationLogJpaRepository.findByBookingId(bookindId);
    }
}