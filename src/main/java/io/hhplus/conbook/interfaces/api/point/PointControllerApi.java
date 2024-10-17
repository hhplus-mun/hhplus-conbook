package io.hhplus.conbook.interfaces.api.point;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Point management", description = "Point management API")
public interface PointControllerApi {

    @Operation(
            summary = "포인트 충전",
            description = "포인트를 충전할 사용자 id와 충전할 양을 파라미터에 넣는다."
    )
    @PatchMapping("/{id}/charge")
    PointResponse.Charge charge(
            @PathVariable long id,
            @RequestBody long amount
    );

    @Operation(
            summary = "포인트 조회",
            description = "사용자 식별자를 통해 해당 사용자의 잔액을 조회"
    )
    @GetMapping("/{id}")
    PointResponse.Point point(
            @PathVariable long id
    );
}
