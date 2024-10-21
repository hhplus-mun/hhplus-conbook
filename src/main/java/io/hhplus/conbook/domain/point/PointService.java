package io.hhplus.conbook.domain.point;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PointService {

    private final UserPointRepository userPointRepository;

    @Transactional
    public UserPoint chargePoint(long userId, long amount, LocalDateTime reqTime) {
        // start Lock
        UserPoint userPoint = userPointRepository.findPointWithPessimisticLock(userId);
        if (userPoint.getUpdatedTime().equals(reqTime)) throw new DuplicateRequestException();

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
        if (userPoint.getUpdatedTime().equals(reqTime)) throw new DuplicateRequestException();

        long totalPoint = userPoint.getPoint() - amount;
        if (totalPoint < 0) throw new IllegalArgumentException("잔고가 부족합니다.");
        userPoint.updatePointWith(totalPoint, reqTime);

        return userPointRepository.update(userPoint);
    }
}
