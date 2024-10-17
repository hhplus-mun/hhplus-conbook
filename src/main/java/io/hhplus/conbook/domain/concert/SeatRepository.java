package io.hhplus.conbook.domain.concert;

import java.util.List;

public interface SeatRepository {
    List<Seat> findAvailableListBy(Long id);

    Seat findSeatWithPessimisticLock(long seatId);

    void updateStatus(Seat seat);
}
