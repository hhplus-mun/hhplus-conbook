package io.hhplus.conbook.domain.payment;

import io.hhplus.conbook.domain.booking.Booking;
import io.hhplus.conbook.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class Payment {
    private Long id;
    private Booking booking;
    private User user;
    private long amount;
    private LocalDateTime paidAt;

    public Payment(Booking booking, User user, long amount) {
        this.booking = booking;
        this.user = user;
        this.amount = amount;
        paidAt = LocalDateTime.now();
    }
}
