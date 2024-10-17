package io.hhplus.conbook.infra.db;

import io.hhplus.conbook.domain.concert.Seat;
import io.hhplus.conbook.domain.concert.SeatRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SeatRepositoryImpl implements SeatRepository {
    @Override
    public List<Seat> findAvailableListBy(Long id) {
        return null;
    }

    @Override
    public Seat findSeatWithPessimisticLock(long seatId) {
        return null;
    }

    @Override
    public void updateStatus(Seat seat) {
    }
}
