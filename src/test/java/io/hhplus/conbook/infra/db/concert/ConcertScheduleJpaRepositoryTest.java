package io.hhplus.conbook.infra.db.concert;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

@SpringBootTest
class ConcertScheduleJpaRepositoryTest {
    @Autowired
    ConcertScheduleJpaRepository scheduleJpaRepository;

    @Test
    @DisplayName("[정상]: JPA 쿼리 조회 - 공연 일정 조회")
    void schedules() {
        long givenId = 1L;

        scheduleJpaRepository.findAllByConcertIdOrderByConcertDate(givenId);
    }
    
    @Test
    @DisplayName("[정상]: JPA 쿼리 조회 - 단일 공연 일정 조회")
    void singleSchedule() {
        long givenId = 1;
        LocalDate date = LocalDate.now();

        scheduleJpaRepository.findByConcertIdAndConcertDate(givenId, date);
    }
}