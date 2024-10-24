package io.hhplus.conbook.infra.db.concert;

import io.hhplus.conbook.domain.concert.ConcertSchedule;
import io.hhplus.conbook.domain.concert.ConcertScheduleRepository;
import io.hhplus.conbook.interfaces.api.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConcertScheduleRepositoryImpl implements ConcertScheduleRepository {

    private final ConcertScheduleJpaRepository concertScheduleJpaRepository;

    @Override
    public List<ConcertSchedule> findScheduleListBy(long concertId) {
        return concertScheduleJpaRepository.findAllByConcertId(concertId)
                .stream()
                .map(ConcertScheduleEntity::toDomain)
                .toList();
    }

    @Override
    public ConcertSchedule findScheduleBy(long concertId, LocalDate date) {
        return concertScheduleJpaRepository.findByConcertIdAndAndConcertDate(concertId, date)
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.CONCERT_SCHEDULE_NOT_FOUND.getCode()))
                .toDomain();
    }

    @Override
    @Transactional
    public ConcertSchedule saveOrUpdate(ConcertSchedule schedule) {
        return concertScheduleJpaRepository.save(new ConcertScheduleEntity(schedule))
                .toDomain();
    }
}
