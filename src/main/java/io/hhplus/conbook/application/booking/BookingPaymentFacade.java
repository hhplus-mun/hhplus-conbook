package io.hhplus.conbook.application.booking;

import io.hhplus.conbook.domain.booking.Booking;
import io.hhplus.conbook.domain.booking.BookingService;
import io.hhplus.conbook.domain.payment.Payment;
import io.hhplus.conbook.domain.payment.PaymentService;
import io.hhplus.conbook.domain.point.PointService;
import io.hhplus.conbook.domain.token.TokenManager;
import io.hhplus.conbook.domain.user.User;
import io.hhplus.conbook.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class BookingPaymentFacade {
    private final BookingService bookingService;
    private final UserService userService;
    private final PointService pointService;
    private final PaymentService paymentService;
    private final TokenManager tokenManager;

    /*
     * 예약건에 대한 결제 메서드
     * 1. 요청한 예약에 대한 결제처리
     * 2. 사용자 포인트 삭감.
     * 3. 결제 이력 저장
     * 4. 액세스 토큰 만료처리
     */
    @Transactional
    public BookingPaymentResult.Paid processPayment(BookingPaymentCommand.Paid paid) {
        Booking booking = bookingService.completePayment(paid.bookingId());
        User user = userService.getUserByUUID(paid.userUUID());
        pointService.spendPoint(user.getId(), booking.getBookingPrice(), paid.reqTime());

        Payment payment = paymentService.savePaymentHistory(booking, user);
        tokenManager.expireAccessRight(paid.concertId(), paid.userUUID());

        return new BookingPaymentResult.Paid(booking.getId(), payment.getAmount(), payment.getPaidAt());
    }
}
