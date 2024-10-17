package io.hhplus.conbook.interfaces.api.booking;

import io.hhplus.conbook.config.CustomAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;

public interface BookingControllerApi {
    @PostMapping("/{id}/payments")
    BookingResponse.Payments payments(
            @PathVariable long id,
            @RequestAttribute(name = CustomAttribute.USER_UUID) String uuid
    );
}
