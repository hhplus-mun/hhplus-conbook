package io.hhplus.conbook.infra.db.token;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TokenHistoryJpaRepository extends JpaRepository<TokenHistoryEntity, Long> {

    @Query("SELECT h FROM TokenHistoryEntity h JOIN h.concert c WHERE c.id = :concertId AND h.userUUID = :uuid")
    List<TokenHistoryEntity> findHistoryBy(@Param("concertId") long concertId, @Param("uuid") String userUUID);
}
