package io.hhplus.conbook.infra.db.concert;

import io.hhplus.conbook.domain.concert.Seat;
import io.hhplus.conbook.domain.concert.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class SeatRepositoryImpl implements SeatRepository {
    private final SeatJpaRepository seatJpaRepository;

    @Override
    public List<Seat> findAvailableListBy(Long scheduleId) {
        return seatJpaRepository.findAllByConcertScheduleId(scheduleId)
                .stream()
                .filter(s -> !s.isOccupied())
                .map(SeatEntity::toDomain)
                .toList();
    }

    @Override
    public Seat findSeatWithPessimisticLock(long seatId) {
        return seatJpaRepository.findByWithPessimisticLock(seatId)
                .orElseThrow(() -> new IllegalArgumentException())
                .toDomain();
    }

    @Override
    public void updateStatus(Seat seat) {
        seatJpaRepository.save(new SeatEntity(seat));
    }
}
