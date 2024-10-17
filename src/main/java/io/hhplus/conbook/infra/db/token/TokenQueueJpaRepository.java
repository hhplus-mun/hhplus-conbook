package io.hhplus.conbook.infra.db.token;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenQueueJpaRepository extends JpaRepository<TokenQueueEntity, Long> {

    @EntityGraph(attributePaths = {"concert"})
    Optional<TokenQueueEntity> findByConcertId(long concertId);
}
