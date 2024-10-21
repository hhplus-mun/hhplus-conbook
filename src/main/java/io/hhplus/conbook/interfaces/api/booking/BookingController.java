package io.hhplus.conbook.interfaces.api.booking;

import io.hhplus.conbook.application.booking.BookingPaymentCommand;
import io.hhplus.conbook.application.booking.BookingPaymentFacade;
import io.hhplus.conbook.application.booking.BookingPaymentResult;
import io.hhplus.conbook.config.CustomAttribute;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/v1/bookings")
@RequiredArgsConstructor
public class BookingController implements BookingControllerApi {

    private final BookingPaymentFacade bookingPaymentFacade;

    /**
     * TODO: 결제 API
     * @param id bookingId
     */
    @Override
    @PostMapping("/{id}/payments")
    public BookingResponse.Payments payments(
            @PathVariable long id,
            @RequestAttribute(name = CustomAttribute.USER_UUID) String uuid,
            @RequestAttribute(name = CustomAttribute.CONCERT_ID) long concertId
    ) {
        BookingPaymentResult.Paid result =
                bookingPaymentFacade.processPayment(
                        BookingPaymentCommand.Paid.builder()
                                .bookingId(id)
                                .concertId(concertId)
                                .userUUID(uuid)
                                .reqTime(LocalDateTime.now())
                                .build()
                );

        return new BookingResponse.Payments(result.bookingId(), result.paymentPrice(), result.paymentTime());
    }
}
