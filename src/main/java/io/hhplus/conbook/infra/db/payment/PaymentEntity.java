package io.hhplus.conbook.infra.db.payment;

import io.hhplus.conbook.domain.payment.Payment;
import io.hhplus.conbook.infra.db.booking.BookingEntity;
import io.hhplus.conbook.infra.db.user.UserEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Table(name = "payment")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id")
    private BookingEntity booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    private long amount;
    @Column(updatable = false)
    private LocalDateTime paidAt;

    public PaymentEntity(Payment payment) {
        this.id = payment.getId();
        this.booking = new BookingEntity(payment.getBooking());
        this.user = new UserEntity(payment.getUser());
        this.amount = payment.getAmount();
        this.paidAt = payment.getPaidAt();
    }

    public Payment toDomain() {
        return new Payment(id, booking.toDomain(), user.toDomain(), amount, paidAt);
    }
}
