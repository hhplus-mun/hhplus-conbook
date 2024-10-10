package io.hhplus.conbook.interfaces.api.booking;

import java.time.LocalDateTime;

public class BookingResponse {
    public record Payments(
            /** 결제 금액 */
            long amount,
            LocalDateTime paymentsTime
    ) {}
}
