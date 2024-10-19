package io.hhplus.conbook.infra.db.user;

import io.hhplus.conbook.domain.user.User;
import io.hhplus.conbook.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository userJpaRepository;

    @Override
    public User getUserBy(long id) {
        return userJpaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException())
                .toDomain();
    }

    @Override
    public User getUserByUUID(String uuid) {
        return userJpaRepository.findByUuid(uuid)
                .orElseThrow(() -> new IllegalArgumentException())
                .toDomain();
    }
}