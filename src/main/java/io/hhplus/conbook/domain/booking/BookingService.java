package io.hhplus.conbook.domain.booking;

import io.hhplus.conbook.domain.concert.Seat;
import io.hhplus.conbook.domain.concert.SeatRepository;
import io.hhplus.conbook.domain.user.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
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
    public Booking createBooking(long seatId, User user) {
        Seat seat = seatRepository.findSeatWithPessimisticLock(seatId);
        if (seat.isOccupied()) throw new AlreadyOccupiedException();

        Booking saved = bookingRepository.save(new Booking(seat, user, BookingStatus.RESERVED));

        // 좌석상태 업데이트
        seat.hasReserved();
        seatRepository.updateStatus(seat);

        return saved;
    }
}