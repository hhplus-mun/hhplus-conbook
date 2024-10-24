package io.hhplus.conbook.domain.booking;

import io.hhplus.conbook.domain.concert.ConcertSchedule;
import io.hhplus.conbook.domain.concert.Seat;
import io.hhplus.conbook.domain.concert.SeatRepository;
import io.hhplus.conbook.domain.user.User;
import io.hhplus.conbook.interfaces.api.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@AllArgsConstructor
@Transactional(readOnly = true)
public class BookingService {
    private final SeatRepository seatRepository;
    private final BookingRepository bookingRepository;

    /**
     * 예약 후 5분 이내로 결제가 이루어지지 않으면 예약을 취소처리한다.
     *
     * @param seatId
     * @param user
     * @return
     */
    @Transactional
    public Booking createBooking(ConcertSchedule schedule, long seatId, User user) {
        Seat seat = seatRepository.findSeatWithPessimisticLock(seatId);
        if (seat.isOccupied()) throw new AlreadyOccupiedException(ErrorCode.NOT_AVAILABLE_SEAT.getCode());

        seat.addSchedule(schedule);

        Booking saved = bookingRepository.save(new Booking(seat, user, BookingStatus.RESERVED));

        // 좌석상태 업데이트
        seat.hasReserved();
        seatRepository.updateStatus(seat);

        return saved;
    }

    @Transactional
    public void checkOrUpdate(long bookingId) {
        Booking found = bookingRepository.findBy(bookingId);

        if (!found.getStatus().equals(BookingStatus.PAID)) {
            Seat seat = found.getSeat();
            seat.hasCancelled();
            seatRepository.updateStatus(seat);

            found.hasCancelled();
            bookingRepository.save(found);
        }
    }

    @Transactional
    public Booking completePayment(long bookingId) {
        Booking booking = bookingRepository.findBy(bookingId);
        if (!booking.getStatus().equals(BookingStatus.RESERVED)) throw new IllegalStateException(ErrorCode.INVALID_BOOKING_STATUS.getCode());

        booking.hasPaid();
        return bookingRepository.save(booking);
    }
}