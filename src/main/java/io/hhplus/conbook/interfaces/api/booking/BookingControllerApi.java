package io.hhplus.conbook.interfaces.api.booking;

import io.hhplus.conbook.config.CustomAttribute;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;

@Tag(name = "Booking management", description = "Booking management API")
@SecurityRequirement(name = "Bearer Authentication")
public interface BookingControllerApi {

    @Operation(
            summary = "결제",
            description = "예약을 결제 처리하고 결제 내역을 생성"
    )
    @PostMapping("/{id}/payments")
    BookingResponse.Payments payments(
            @PathVariable long id,
            @RequestAttribute(name = CustomAttribute.USER_UUID) String uuid,
            @RequestAttribute(name = CustomAttribute.CONCERT_ID) long concertId
    );
}
