package io.hhplus.conbook.infra.db.concert;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ConcertScheduleJpaRepository extends JpaRepository<ConcertScheduleEntity, Long> {

    @EntityGraph(attributePaths = {"concert"})
    List<ConcertScheduleEntity> findAllByConcertId(long concertId);

    Optional<ConcertScheduleEntity> findByConcertIdAndAndConcertDate(long concertId, LocalDate date);
}
