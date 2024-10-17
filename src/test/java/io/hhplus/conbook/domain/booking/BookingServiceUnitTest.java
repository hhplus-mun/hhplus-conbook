package io.hhplus.conbook.domain.booking;

import io.hhplus.conbook.domain.concert.Seat;
import io.hhplus.conbook.domain.concert.SeatRepository;
import io.hhplus.conbook.domain.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class BookingServiceUnitTest {
    @InjectMocks
    BookingService bookingService;

    @Mock
    SeatRepository seatRepository;
    @Mock
    BookingRepository bookingRepository;
    @Mock
    TaskScheduler taskScheduler;

    @Test
    @DisplayName("[정상]: 예약처리")
    void createBook() {
        // given
        long seatId = 1L;
        User user = new User(1L, "hhp", UUID.randomUUID().toString());
        Seat seat = new Seat(1L, null, false, "A", 1, 1000);

        given(seatRepository.findSeatWithPessimisticLock(1L))
                .willReturn(seat);
        Booking booking = new Booking(seat, user, BookingStatus.RESERVED);
        ReflectionTestUtils.setField(booking, "id", 1L);
        given(bookingRepository.save(booking))
                .willReturn(booking);

        // when
        Booking result = bookingService.createBooking(null, seatId, user);

        // then
        assertThat(result.getId()).isEqualTo(1L);
    }
}