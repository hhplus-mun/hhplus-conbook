package io.hhplus.conbook.domain.booking;

import io.hhplus.conbook.domain.concert.ConcertSchedule;
import io.hhplus.conbook.domain.concert.SeatRepository;
import io.hhplus.conbook.domain.user.User;
import io.hhplus.conbook.domain.user.UserRepository;
import io.hhplus.conbook.infra.db.concert.ConcertScheduleJpaRepository;
import io.hhplus.conbook.infra.db.concert.SeatEntity;
import io.hhplus.conbook.infra.db.concert.SeatJpaRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BookingServiceTest {
    @Autowired
    BookingService bookingService;

    @Autowired
    UserRepository userRepository;
    @Autowired
    SeatJpaRepository seatJpaRepository;
    @Autowired
    ConcertScheduleJpaRepository concertScheduleJpaRepository;


    @Test
    @DisplayName("[정상]: 통합테스트 - 좌석 예약 기능")
    @Transactional
    void create() {
        // given
        long seatId = 1L;
        User user = userRepository.getUserBy(1L);
        ConcertSchedule schedule = concertScheduleJpaRepository.findById(1L).get().toDomain();

        // when
        Booking booking = bookingService.createBooking(schedule, seatId, user);

        //
        SeatEntity seatEntity = seatJpaRepository.findById(seatId).get();

        assertThat(seatEntity.isOccupied()).isTrue();
        assertThat(booking.getUser().getUuid()).isEqualTo(user.getUuid());
    }

}