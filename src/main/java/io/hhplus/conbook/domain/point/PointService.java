package io.hhplus.conbook.domain.point;

import io.hhplus.conbook.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PointService {

    private final UserPointRepository userPointRepository;

    @Transactional
    public UserPoint chargePoint(User user, long amount, LocalDateTime reqTime) {
        // start Lock
        UserPoint userPoint = userPointRepository.findPointWithPessimisticLock(user.getId());
        if (userPoint.getUpdatedTime().equals(reqTime)) throw new DuplicateRequestException();

        long totalPoint = userPoint.getPoint() + amount;
        userPoint.updatePointWith(totalPoint, reqTime);

        return userPointRepository.update(userPoint);
    }

    public UserPoint getBalance(long userId) {
        return userPointRepository.getUserPoint(userId);
    }


}
