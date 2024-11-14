package io.hhplus.conbook.application.event;

import io.hhplus.conbook.domain.booking.Booking;
import io.hhplus.conbook.domain.booking.BookingStatus;
import io.hhplus.conbook.infra.db.booking.BookingEntity;
import io.hhplus.conbook.infra.db.booking.BookingJpaRepository;
import io.hhplus.conbook.infra.db.concert.SeatEntity;
import io.hhplus.conbook.infra.db.concert.SeatJpaRepository;
import io.hhplus.conbook.infra.db.user.UserEntity;
import io.hhplus.conbook.infra.db.user.UserJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 * ConcertBookingEvent의 Publisher & Listener의 테스트용 클래스
 * - 테스트를 수행하는 클래스 내부에 @Transactional을 추가한 메서드를 돌릴 때 별도의 트랜잭션을 가질 수 없음.
 */
@Component
public class ConcertBookingEventPublisherTestSupporter {
    @Autowired
    ConcertBookingEventPublisher eventPublisher;
    @Autowired
    BookingJpaRepository bookingJpaRepository;
    @Autowired
    SeatJpaRepository seatJpaRepository;
    @Autowired
    UserJpaRepository userJpaRepository;

    @Transactional
    void publishEventInTransaction(long bookingId) {
        SeatEntity seat = seatJpaRepository.findById(30L).get();
        UserEntity user = userJpaRepository.findById(1L).get();
        Booking booking = new Booking(seat.toDomain(), user.toDomain(), BookingStatus.RESERVED);
        ReflectionTestUtils.setField(booking, "id", bookingId);

        Booking bookingResult = bookingJpaRepository.findById(bookingId)
                .orElse(new BookingEntity(booking))
                .toDomain();

        eventPublisher.publishEventOn(bookingResult);
    }
}