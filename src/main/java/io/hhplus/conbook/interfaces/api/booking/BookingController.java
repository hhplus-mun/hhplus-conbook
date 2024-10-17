package io.hhplus.conbook.interfaces.api.booking;

import io.hhplus.conbook.application.booking.BookingCommand;
import io.hhplus.conbook.application.booking.BookingFacade;
import io.hhplus.conbook.application.booking.BookingResult;
import io.hhplus.conbook.config.CustomAttribute;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/v1/bookings")
@RequiredArgsConstructor
public class BookingController implements BookingControllerApi {

    private final BookingFacade bookingFacade;

    /**
     * TODO: 결제 API
     * @param id bookingId
     */
    @Override
    @PostMapping("/{id}/payments")
    public BookingResponse.Payments payments(
            @PathVariable long id,
            @RequestAttribute(name = CustomAttribute.USER_UUID) String uuid
    ) {

        BookingResult.Paid result = bookingFacade.processPayment(new BookingCommand.Paid(id, uuid));

        return new BookingResponse.Payments(1000L, LocalDateTime.now());
    }
}
