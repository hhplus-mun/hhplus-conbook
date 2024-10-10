package io.hhplus.conbook.interfaces.api.point;

import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/v1/point")
public class PointController {

    /**
     * TODO: 잔액 충전 API
     * @param id userId
     * @param amount point
     */
    @PatchMapping("/{id}/charge")
    public PointResponse.Charge charge(
            @PathVariable long id,
            @RequestBody long amount
    ) {
        return new PointResponse.Charge(1L, 1000L, LocalDateTime.now());
    }

    /**
     * TODO: 잔액 조회 API
     * @param id userId
     */
    @GetMapping("/{id}")
    public PointResponse.Point point(
            @PathVariable long id
    ) {
        return new PointResponse.Point(1L, 1000L, LocalDateTime.now());
    }
}
