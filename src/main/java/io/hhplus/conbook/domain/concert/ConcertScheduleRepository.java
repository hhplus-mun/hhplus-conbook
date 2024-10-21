package io.hhplus.conbook.domain.concert;

import java.time.LocalDate;
import java.util.List;

public interface ConcertScheduleRepository {
    List<ConcertSchedule> findScheduleListBy(long concertId);
    ConcertSchedule findScheduleBy(long concertId, LocalDate date);
    ConcertSchedule saveOrUpdate(ConcertSchedule schedule);
}
