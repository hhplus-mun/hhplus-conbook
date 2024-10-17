package io.hhplus.conbook.interfaces.api.point;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Point management", description = "Point management API")
@SecurityRequirement(name = "Bearer Authentication")
public interface PointControllerApi {

    @Operation(
            summary = "잔액 충전",
            description = "결제에 사용될 금액을 API를 통해 충전합니다."
    )
    @PatchMapping("/{id}/charge")
    PointResponse.Charge charge(
            @PathVariable long id,
            @RequestBody long amount
    );

    @Operation(
            summary = "잔액 조회",
            description = "사용자 식별자를 통해 해당 사용자의 잔액을 조회"
    )
    @GetMapping("/{id}")
    PointResponse.Point point(
            @PathVariable long id
    );
}
