package io.hhplus.conbook.infra.db.concert;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConcertScheduleJpaRepository extends JpaRepository<ConcertScheduleEntity, Long> {

    @EntityGraph(attributePaths = {"concert"})
    List<ConcertScheduleEntity> findAllByConcertId(long concertId);
}
