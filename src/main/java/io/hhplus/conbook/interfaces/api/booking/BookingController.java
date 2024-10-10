package io.hhplus.conbook.interfaces.api.booking;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/v1/bookings")
public class BookingController {

    /**
     * TODO: 결제 API
     * @param id bookingId
     */
    @PostMapping("/{id}/payments")
    public BookingResponse.Payments payments(
            @PathVariable long id
    ) {
        return new BookingResponse.Payments(1000L, LocalDateTime.now());
    }
}
