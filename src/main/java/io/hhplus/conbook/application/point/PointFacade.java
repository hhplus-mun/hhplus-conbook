package io.hhplus.conbook.application.point;

import io.hhplus.conbook.domain.point.PointService;
import io.hhplus.conbook.domain.point.UserPoint;
import io.hhplus.conbook.domain.user.User;
import io.hhplus.conbook.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PointFacade {
    private final UserService userService;
    private final PointService pointService;

    public PointResult.Charge chargePoint(PointCommand.Charge charge) {
        User user = userService.getUser(charge.userId());
        UserPoint userPoint = pointService.chargePoint(user, charge.amount(), charge.reqTime());

        return new PointResult.Charge(
                userPoint.getUser().getId(),
                userPoint.getPoint(),
                userPoint.getUpdatedTime()
        );
    }

    public PointResult.Balance pointBalance(PointCommand.Balance balance) {

        UserPoint userPoint = pointService.getBalance(balance.userId());

        return new PointResult.Balance(
                balance.userId(),
                userPoint.getPoint(),
                userPoint.getUpdatedTime()
        );
    }
}
