package io.hhplus.conbook.infra.db.point;

import io.hhplus.conbook.domain.point.UserPoint;
import io.hhplus.conbook.domain.point.UserPointRepository;
import io.hhplus.conbook.domain.user.User;
import io.hhplus.conbook.infra.db.user.UserEntity;
import io.hhplus.conbook.interfaces.api.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserPointRepositoryImpl implements UserPointRepository {

    private final UserPointJpaRepository userPointJpaRepository;

    @Override
    public UserPoint findPointWithPessimisticLock(Long userId) {

        return userPointJpaRepository.findByWithPessimisticLock(userId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.POINT_INFO_NOT_FOUND.getCode()))
                .toDomain();
    }

    @Override
    public UserPoint update(UserPoint userPoint) {
        User user = userPoint.getUser();
        UserEntity userEntity = new UserEntity(user.getId(), user.getName(), user.getUuid());

        // 병합
        return userPointJpaRepository.save(
                new UserPointEntity(userPoint.getId(), userEntity, userPoint.getPoint(), userPoint.getUpdatedTime())).toDomain();
    }

    @Override
    public UserPoint getUserPoint(long userId) {
        return userPointJpaRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.POINT_INFO_NOT_FOUND.getCode()))
                .toDomain();
    }
}
