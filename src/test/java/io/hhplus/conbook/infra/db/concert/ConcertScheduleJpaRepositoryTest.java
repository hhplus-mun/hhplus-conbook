package io.hhplus.conbook.infra.db.concert;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class ConcertScheduleJpaRepositoryTest {
    @Autowired
    ConcertScheduleJpaRepository scheduleJpaRepository;

    @Test
    @DisplayName("[정상]: JPA 쿼리 조회 - 공연 일정 조회")
    void schedules() {
        long givenId = 1L;

        scheduleJpaRepository.findAllByConcertId(givenId);
    }
}