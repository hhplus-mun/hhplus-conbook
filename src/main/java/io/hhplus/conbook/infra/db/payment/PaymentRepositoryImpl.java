package io.hhplus.conbook.infra.db.payment;

import io.hhplus.conbook.domain.payment.Payment;
import io.hhplus.conbook.domain.payment.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepository {
    private final PaymentJpaRepository paymentJpaRepository;

    @Override
    public Payment save(Payment payment) {
        return paymentJpaRepository.save(new PaymentEntity(payment))
                .toDomain();
    }
}
