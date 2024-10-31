package io.hhplus.conbook.domain.concert;

import java.util.List;

public interface SeatRepository {
    List<Seat> findAvailableListBy(Long scheduleId);

    Seat findSeatWithPessimisticLock(long seatId, long scheduleId);

    Seat findSeatBy(long seatId, long scheduleId);

    void updateStatus(Seat seat);
}
