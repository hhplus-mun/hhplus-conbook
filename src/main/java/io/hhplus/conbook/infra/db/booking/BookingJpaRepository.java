package io.hhplus.conbook.infra.db.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface BookingJpaRepository extends JpaRepository<BookingEntity, Long> {

    @Query("""
        SELECT b FROM BookingEntity b
        JOIN FETCH b.user u
        JOIN FETCH b.seat s
        JOIN FETCH s.concertSchedule cs
        JOIN FETCH cs.concert c
        WHERE
            b.id = :id
    """)
    @Override
    Optional<BookingEntity> findById(Long id);
}
