package io.hhplus.conbook.infra.db.concert;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SeatJpaRepositoryTest {
    @Autowired
    SeatJpaRepository seatJpaRepository;

    @Test
    @DisplayName("[정상]: JPA 쿼리 조회 - 해당 스케쥴에 가능한 좌석 조회")
    void queryToFindAvailableSeatsByConcertId() {
        long scheduleId = 1L;

        seatJpaRepository.findAvailableSeatListByScheduleId(scheduleId);
    }
}