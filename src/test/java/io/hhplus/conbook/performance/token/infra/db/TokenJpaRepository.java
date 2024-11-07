package io.hhplus.conbook.performance.token.infra.db;

import io.hhplus.conbook.domain.token.TokenStatus;
import io.hhplus.conbook.domain.token.TokenStatusCount;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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
        WHERE q.concert.id = :concertId and t.tokenValue = :tokenValue
    """)
    Optional<TokenEntity> findTokenByConcertIdAndTokenValue(@Param("concertId") long concertId, @Param("tokenValue") String tokenValue);

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

    @Query("SELECT t.tokenValue FROM TokenEntity t JOIN t.tokenQueue q WHERE q.concert.id = :concertId ORDER BY t.createdAt ASC")
    List<String> findWaitingTokenList(@Param("concertId") long concertId);

    @Query("DELETE FROM TokenEntity t WHERE t.tokenQueue.concert.id = :concertId AND t.tokenValue = :tokenValue")
    @Modifying
    void deleteBy(@Param("concertId") long concertId, @Param("tokenValue") String tokenValue);

    @Query("SELECT t.tokenValue FROM TokenEntity t JOIN t.tokenQueue q WHERE q.concert.id = :concertId ORDER BY t.createdAt ASC")
    List<String> findAllByConcertId(long concertId);

    @Modifying
    @Query("""
        DELETE FROM TokenEntity t
        WHERE
            t.tokenQueue.concert.id = :concertId
        AND
            t.status = :status
        AND
            t.expiredAt < NOW()
    """)
    void removeNonValidTokenBy(
            @Param("concertId") Long concertId,
            @Param("status") TokenStatus status
    );
}
