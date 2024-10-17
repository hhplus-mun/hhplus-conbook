package io.hhplus.conbook.domain.concert;

import java.util.List;

public interface SeatRepository {
    List<Seat> findAvailableListBy(Long scheduleId);

    Seat findSeatWithPessimisticLock(long seatId);

    void updateStatus(Seat seat);
}
