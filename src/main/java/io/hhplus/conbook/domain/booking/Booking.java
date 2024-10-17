package io.hhplus.conbook.domain.booking;

import io.hhplus.conbook.domain.concert.Seat;
import io.hhplus.conbook.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class Booking {
    private static int BOOKING_EXPIRATION_MIN = 5;

    private Long id;
    private Seat seat;
    private User user;
    private BookingStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime expiredAt;

    public Booking(Seat seat, User user, BookingStatus status) {
        this.seat = seat;
        this.user = user;
        this.status = status;
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        expiredAt = createdAt.plusMinutes(BOOKING_EXPIRATION_MIN);
    }
}