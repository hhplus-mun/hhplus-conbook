package io.hhplus.conbook.infra.db.concert;

import io.hhplus.conbook.domain.concert.Seat;
import io.hhplus.conbook.domain.concert.SeatRepository;
import io.hhplus.conbook.interfaces.api.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SeatRepositoryImpl implements SeatRepository {
    private final SeatJpaRepository seatJpaRepository;

    @Override
    public List<Seat> findAvailableListBy(Long scheduleId) {
        return seatJpaRepository.findAvailableSeatListByScheduleId(scheduleId)
                .stream()
                .map(SeatEntity::toDomain)
                .toList();
    }

    @Override
    public Seat findSeatWithPessimisticLock(long seatId, long scheduleId) {
        return seatJpaRepository.findByWithPessimisticLock(seatId, scheduleId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.SEAT_NOT_FOUND.getCode()))
                .toDomainWithoutSchedule();
    }

    @Override
    public Seat findSeatBy(long seatId, long scheduleId) {

        return seatJpaRepository.findBy(seatId, scheduleId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.SEAT_NOT_FOUND.getCode()))
                .toDomainWithoutSchedule();
    }

    @Override
    @Transactional
    public void updateStatus(Seat seat) {
        seatJpaRepository.save(new SeatEntity(seat));
    }
}
