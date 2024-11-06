package io.hhplus.conbook.infra.db.token;

import io.hhplus.conbook.domain.token.TokenStatusCount;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TokenJpaRepository extends JpaRepository<TokenEntity, Integer> {
    @EntityGraph(attributePaths = {"tokenQueue"})
    List<TokenEntity> findAllByTokenQueueId(Long id);

    @Query("""
        SELECT t FROM TokenEntity t
        JOIN FETCH t.tokenQueue q
        WHERE q.concert.id = :concertId and t.userUUID = :uuid
    """)
    Optional<TokenEntity> findTokenByConcertIdAndUUID(@Param("concertId") long concertId, @Param("uuid") String uuid);

    @Query("""
        SELECT t FROM TokenEntity t
        WHERE t.tokenQueue.id = :queueId AND t.userUUID = :uuid
    """)
    Optional<TokenEntity> findTokenByQueueIdAndUUID(@Param("queueId") long queueId, @Param("uuid") String uuid);

    @Query("""
        SELECT
            new io.hhplus.conbook.domain.token.TokenStatusCount(t.status, CAST(count(t) AS INTEGER))
        FROM
            TokenEntity t
        JOIN
            t.tokenQueue q
        WHERE
            q.concert.id = :concertId
        GROUP BY t.status
    """)
    List<TokenStatusCount> findTokenStatusCountBy(@Param("concertId") long concertId);
}
