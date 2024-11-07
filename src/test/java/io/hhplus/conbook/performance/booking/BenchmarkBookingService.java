package io.hhplus.conbook.performance.booking;

import io.hhplus.conbook.domain.booking.AlreadyOccupiedException;
import io.hhplus.conbook.domain.booking.Booking;
import io.hhplus.conbook.domain.booking.BookingRepository;
import io.hhplus.conbook.domain.booking.BookingStatus;
import io.hhplus.conbook.domain.concert.ConcertSchedule;
import io.hhplus.conbook.domain.concert.Seat;
import io.hhplus.conbook.domain.concert.SeatRepository;
import io.hhplus.conbook.domain.user.User;
import io.hhplus.conbook.infra.redis.RedisLockRepository;
import io.hhplus.conbook.interfaces.api.ErrorCode;
import org.springframework.transaction.annotation.Transactional;

public class BenchmarkBookingService {
    private final SeatRepository seatRepository;
    private final BookingRepository bookingRepository;
    private final RedisLockRepository redisLockRepository;

    public BenchmarkBookingService(SeatRepository seatRepository, BookingRepository bookingRepository, RedisLockRepository redisLockRepository) {
        this.seatRepository = seatRepository;
        this.bookingRepository = bookingRepository;
        this.redisLockRepository = redisLockRepository;
    }

    /**
     * 비관적 락 사용
     */
    @Transactional
    public Booking createBookingUsingPessimisticLock(ConcertSchedule schedule, long seatId, User user) {
        Seat seat = seatRepository.findSeatWithPessimisticLock(seatId, schedule.getId());
        if (seat.isOccupied()) throw new AlreadyOccupiedException(ErrorCode.NOT_AVAILABLE_SEAT.getCode());

        seat.addSchedule(schedule);

        Booking saved = bookingRepository.save(new Booking(seat, user, BookingStatus.RESERVED));

        // 좌석상태 업데이트
        seat.hasReserved();
        seatRepository.updateStatus(seat);

        return saved;
    }

    /**
     * 분산락을 사용하고 좌석은 별도의 락없이 조회
     */
    public Booking createBookingUsingDistributedLock(ConcertSchedule schedule, long seatId, User user) {
        String key = String.format("lock:seatId:%d", seatId);
        try {
            if (!redisLockRepository.lock(key)) {
                throw new IllegalStateException();
            }
            return createBooking(schedule, seatId, user);
        } finally {
            redisLockRepository.unlock(key);
        }
    }

    /**
     *
     */
    @Transactional
    protected Booking createBooking(ConcertSchedule schedule, long seatId, User user) {
        Seat seat = seatRepository.findSeatBy(seatId, schedule.getId());
        if (seat.isOccupied()) throw new AlreadyOccupiedException(ErrorCode.NOT_AVAILABLE_SEAT.getCode());

        seat.addSchedule(schedule);

        Booking saved = bookingRepository.save(new Booking(seat, user, BookingStatus.RESERVED));

        // 좌석상태 업데이트
        seat.hasReserved();
        seatRepository.updateStatus(seat);

        return saved;
    }
}
