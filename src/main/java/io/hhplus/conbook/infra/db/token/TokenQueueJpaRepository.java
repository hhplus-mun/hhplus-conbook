package io.hhplus.conbook.infra.db.token;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TokenQueueJpaRepository extends JpaRepository<TokenQueueEntity, Long> {

    @EntityGraph(attributePaths = {"concert"})
    Optional<TokenQueueEntity> findByConcertId(long concertId);

    @Query("select q from TokenQueueEntity q join fetch q.tokens t")
    List<TokenQueueEntity> findAllWithTokens();
}
