package io.hhplus.conbook.infra.db;

import io.hhplus.conbook.domain.concert.ConcertSchedule;
import io.hhplus.conbook.domain.concert.ConcertScheduleRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public class ConcertScheduleRepositoryImpl implements ConcertScheduleRepository {
    @Override
    public ConcertSchedule findScheduleBy(long concertId, LocalDate date) {
        return null;
    }

    @Override
    public void updateSchedule(ConcertSchedule schedule) {

    }
}
