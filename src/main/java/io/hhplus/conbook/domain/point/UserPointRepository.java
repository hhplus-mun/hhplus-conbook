package io.hhplus.conbook.domain.point;

public interface UserPointRepository {
    UserPoint findPointWithPessimisticLock(Long userId);
    UserPoint update(UserPoint userPoint);

    UserPoint getUserPoint(long userId);
}
