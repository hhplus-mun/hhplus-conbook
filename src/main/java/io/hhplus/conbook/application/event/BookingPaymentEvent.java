package io.hhplus.conbook.application.event;

import io.hhplus.conbook.domain.payment.Payment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@ToString
public class BookingPaymentEvent {
    private long paymentId;
    private long bookingId;
    private long userId;
    private String userName;
    private long amount;
    private LocalDateTime paidAt;

    public BookingPaymentEvent(Payment payment) {
        this.paymentId = payment.getId();
        this.bookingId = payment.getBooking().getId();
        this.userId = payment.getUser().getId();
        this.userName = payment.getUser().getName();
        this.amount = payment.getAmount();
        this.paidAt = payment.getPaidAt();
    }
}
