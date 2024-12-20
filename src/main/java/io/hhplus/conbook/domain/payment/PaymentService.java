package io.hhplus.conbook.domain.payment;

import io.hhplus.conbook.domain.booking.Booking;
import io.hhplus.conbook.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentService {

    private final PaymentRepository paymentRepository;

    @Transactional
    public Payment savePaymentHistory(Booking booking, User user) {
        Payment payment = new Payment(booking, user, booking.getBookingPrice());

        return paymentRepository.save(payment);
    }
}
