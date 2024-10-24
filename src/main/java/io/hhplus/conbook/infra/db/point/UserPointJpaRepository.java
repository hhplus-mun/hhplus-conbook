package io.hhplus.conbook.infra.db.point;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserPointJpaRepository extends JpaRepository<UserPointEntity, Long> {

    @EntityGraph(attributePaths = {"user"})
    Optional<UserPointEntity> findByUserId(Long userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select up from UserPointEntity up join fetch up.user u where u.id = :userId")
    Optional<UserPointEntity> findByWithPessimisticLock(@Param("userId") Long userId);
}
