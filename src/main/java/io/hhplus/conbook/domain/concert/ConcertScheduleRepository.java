package io.hhplus.conbook.domain.concert;

import java.time.LocalDate;

public interface ConcertScheduleRepository {
    ConcertSchedule findScheduleBy(long concertId, LocalDate date);

    void updateSchedule(ConcertSchedule schedule);
}
