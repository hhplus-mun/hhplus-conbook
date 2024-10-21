package io.hhplus.conbook.infra.db.payment;

import io.hhplus.conbook.domain.payment.Payment;
import io.hhplus.conbook.domain.payment.PaymentRepository;
import io.hhplus.conbook.infra.db.booking.BookingEntity;
import io.hhplus.conbook.infra.db.user.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepository {
    private final PaymentJpaRepository paymentJpaRepository;
    @Override
    public Payment save(Payment payment) {
        BookingEntity bookingEntity = new BookingEntity(payment.getBooking());
        UserEntity userEntity = new UserEntity(payment.getUser());
        return paymentJpaRepository.save(
                new PaymentEntity(bookingEntity, userEntity, payment.getAmount(), payment.getPaidAt())
        ).toDomain();
    }
}
