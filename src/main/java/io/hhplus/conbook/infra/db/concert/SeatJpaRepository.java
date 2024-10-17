package io.hhplus.conbook.infra.db.concert;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.List;
import java.util.Optional;

public interface SeatJpaRepository extends JpaRepository<SeatEntity, Long> {
    @EntityGraph(attributePaths = {"concertSchedule"})
    List<SeatEntity> findAllByConcertScheduleId(Long scheduleId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<SeatEntity> findByWithPessimisticLock(long seatId);

}
