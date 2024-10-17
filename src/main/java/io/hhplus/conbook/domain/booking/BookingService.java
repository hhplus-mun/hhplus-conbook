package io.hhplus.conbook.domain.booking;

import io.hhplus.conbook.domain.concert.Seat;
import io.hhplus.conbook.domain.concert.SeatRepository;
import io.hhplus.conbook.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
@Slf4j
@AllArgsConstructor
public class BookingService {
    /**
     * 예약시 결제 이전까지 점유할 수 있는 시간 - 5분
     */
    private final int DEFAULT_BOOKING_STAGING_MIN = 5;

    private final SeatRepository seatRepository;
    private final BookingRepository bookingRepository;
    private final TaskScheduler taskScheduler;

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

    public void addSchedule(Booking booking) {
        // 단발성 스케쥴러 등록
        Runnable task = () -> {
            log.info("\nTaskScheduler has been executed");

            Booking found = bookingRepository.findBy(booking.getId());

            if (!found.getStatus().equals(BookingStatus.PAID)) {
                Seat seat = booking.getSeat();
                seat.hasCancelled();

                seatRepository.updateStatus(seat);
            }
        };
        Instant startTime =
                LocalDateTime.now().plusMinutes(DEFAULT_BOOKING_STAGING_MIN).atZone(ZoneId.systemDefault()).toInstant();

        taskScheduler.schedule(task,startTime);
    }
}