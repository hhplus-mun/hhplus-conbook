package io.hhplus.conbook.interfaces.api.point;

import io.hhplus.conbook.application.point.PointCommand;
import io.hhplus.conbook.application.point.PointFacade;
import io.hhplus.conbook.application.point.PointResult;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/v1/point")
@RequiredArgsConstructor
public class PointController implements PointControllerApi {

    private final PointFacade pointFacade;

    /**
     * 잔액 충전 API
     *
     * @param id userId
     * @param amount point
     */
    @Override
    @PatchMapping("/{id}/charge")
    public PointResponse.Charge charge(
            @PathVariable long id,
            @RequestBody long amount
    ) {
        PointResult.Charge result = pointFacade.chargePoint(new PointCommand.Charge(id, amount, LocalDateTime.now()));

        return new PointResponse.Charge(
                result.userId(),
                result.point(),
                result.updatedTime());
    }

    /**
     * 잔액 조회 API
     *
     * @param id userId
     */
    @Override
    @GetMapping("/{id}")
    public PointResponse.Point point(
            @PathVariable long id
    ) {
        PointResult.Balance balance = pointFacade.pointBalance(new PointCommand.Balance(id));

        return new PointResponse.Point(
                balance.userId(),
                balance.point(),
                balance.lastUpdatedTime()
        );
    }
}
