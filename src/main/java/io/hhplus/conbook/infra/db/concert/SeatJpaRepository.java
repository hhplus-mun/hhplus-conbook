package io.hhplus.conbook.infra.db.concert;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SeatJpaRepository extends JpaRepository<SeatEntity, Long> {
    @Query("""
        SELECT s FROM SeatEntity s
        WHERE
            s.concertSchedule.id = :scheduleId
        AND
            s.isOccupied = false
        ORDER BY
            s.rowName, s.seatNo
    """)
    List<SeatEntity> findAvailableSeatListByScheduleId(Long scheduleId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from SeatEntity s where s.id = :id and s.concertSchedule.id = :scheduleId")
    Optional<SeatEntity> findByWithPessimisticLock(@Param("id") long id, @Param("scheduleId") long scheduleId);

    @Query("select s from SeatEntity s where s.id = :id and s.concertSchedule.id = :scheduleId")
    Optional<SeatEntity> findBy(@Param("id") long id, @Param("scheduleId") long scheduleId);
}
