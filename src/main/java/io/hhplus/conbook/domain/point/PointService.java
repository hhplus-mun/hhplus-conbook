package io.hhplus.conbook.domain.point;

import io.hhplus.conbook.interfaces.api.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PointService {

    private final UserPointRepository userPointRepository;

    @Transactional
    public UserPoint chargePoint(long userId, long amount, LocalDateTime reqTime) {
        // start Lock
        UserPoint userPoint = userPointRepository.findPointWithPessimisticLock(userId);
        if (isReqTimeValid(reqTime, userPoint.getUpdatedTime()))
            throw new NotValidRequestException(ErrorCode.INVALID_POINT_REQUEST.getCode());

        long totalPoint = userPoint.getPoint() + amount;
        userPoint.updatePointWith(totalPoint, reqTime);

        return userPointRepository.update(userPoint);
    }

    public UserPoint getBalance(long userId) {
        return userPointRepository.getUserPoint(userId);
    }

    @Transactional
    public UserPoint spendPoint(Long userId, long amount, LocalDateTime reqTime) {
        // start Lock
        UserPoint userPoint = userPointRepository.findPointWithPessimisticLock(userId);
        if (isReqTimeValid(reqTime, userPoint.getUpdatedTime()))
            throw new NotValidRequestException(ErrorCode.INVALID_POINT_REQUEST.getCode());

        long totalPoint = userPoint.getPoint() - amount;
        if (totalPoint < 0) throw new IllegalStateException(ErrorCode.INSUFFICIENT_POINT_BALANCE.getCode());
        userPoint.updatePointWith(totalPoint, reqTime);

        return userPointRepository.update(userPoint);
    }

    private boolean isReqTimeValid(LocalDateTime reqTime, LocalDateTime updatedTime) {
        return updatedTime.equals(reqTime) || updatedTime.isAfter(reqTime);
    }
}
