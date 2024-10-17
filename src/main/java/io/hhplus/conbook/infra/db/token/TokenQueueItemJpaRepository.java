package io.hhplus.conbook.infra.db.token;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TokenQueueItemJpaRepository extends JpaRepository<TokenQueueItemEntity, Long> {
    @EntityGraph(attributePaths = {"user", "tokenQueue"})
    List<TokenQueueItemEntity> findAllByTokenQueueId(Long id);

    @Query("select i from TokenQueueItemEntity i " +
            "join fetch i.user u " +
            "join fetch i.tokenQueue q " +
            "where q.concert.id = :concertId and u.uuid = :uuid")
    Optional<TokenQueueItemEntity> findItemByConcertIdAndUUID(@Param("concertId") long concertId, @Param("uuid") String uuid);
}
