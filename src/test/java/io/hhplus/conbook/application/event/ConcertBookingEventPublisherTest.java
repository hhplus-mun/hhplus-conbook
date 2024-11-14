package io.hhplus.conbook.application.event;

import io.hhplus.conbook.infra.db.client.NotificationLogEntity;
import io.hhplus.conbook.infra.db.client.NotificationLogJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ConcertBookingEventPublisherTest {

    @Autowired
    ConcertBookingEventPublisherTestSupporter eventPublisher;
    @Autowired
    NotificationLogJpaRepository notificationLogJpaRepository;

    @BeforeEach
    void clear() {
        notificationLogJpaRepository.deleteAll();
    }

    @Test
    @DisplayName("[정상]: 좌석예약 시 이벤트가 정상적으로 발생시키고 처리한다.")
    void publishConcertBookingEvent() throws Exception {
        // given
        long bookingId = 1L;

        // when
        eventPublisher.publishEventInTransaction(bookingId);

        // then
        System.out.println("WAIT");
        Thread.sleep(500);

        Optional<NotificationLogEntity> foundLog = notificationLogJpaRepository.findByBookingId(bookingId);
        assertThat(foundLog.isPresent()).isTrue();
    }
}