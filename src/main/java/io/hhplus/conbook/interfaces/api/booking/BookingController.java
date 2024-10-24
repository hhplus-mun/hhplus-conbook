package io.hhplus.conbook.interfaces.api.booking;

import io.hhplus.conbook.application.booking.BookingPaymentCommand;
import io.hhplus.conbook.application.booking.BookingPaymentFacade;
import io.hhplus.conbook.application.booking.BookingPaymentResult;
import io.hhplus.conbook.interfaces.api.ApiRoutes;
import io.hhplus.conbook.interfaces.filter.CustomAttribute;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping(ApiRoutes.BASE_BOOKING_API_PATH)
@RequiredArgsConstructor
public class BookingController implements BookingControllerApi {

    private final BookingPaymentFacade bookingPaymentFacade;

    /**
     * TODO: 결제 API
     * @param id bookingId
     */
    @Override
    @PostMapping("/{id}/payment")
    public BookingResponse.Payment payment(
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

        return new BookingResponse.Payment(result.bookingId(), result.paymentPrice(), result.paymentTime());
    }
}
