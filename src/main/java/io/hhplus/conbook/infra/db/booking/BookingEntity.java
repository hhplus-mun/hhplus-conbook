package io.hhplus.conbook.infra.db.booking;

import io.hhplus.conbook.domain.booking.Booking;
import io.hhplus.conbook.domain.booking.BookingStatus;
import io.hhplus.conbook.domain.concert.Seat;
import io.hhplus.conbook.domain.user.User;
import io.hhplus.conbook.infra.db.concert.SeatEntity;
import io.hhplus.conbook.infra.db.user.UserEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Optional;

@Table(name = "booking")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BookingEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id")
    private SeatEntity seat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    private int bookingPrice;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    @Column(updatable = false)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime expiredAt;

    public Booking toDomain() {
        return new Booking(id, seat.toDomain(), user.toDomain(), bookingPrice, status, createdAt, updatedAt, expiredAt);
    }

    public BookingEntity(Booking booking) {
        this.id = booking.getId();
        this.bookingPrice = booking.getBookingPrice();
        this.status = booking.getStatus();
        this.createdAt = booking.getCreatedAt();
        this.updatedAt = booking.getUpdatedAt();
        this.expiredAt = booking.getExpiredAt();

        Optional<Seat> givenSeat = Optional.ofNullable(booking.getSeat());
        if (givenSeat.isPresent()) {
            this.seat = new SeatEntity(givenSeat.get());
        }
        Optional<User> givenUser = Optional.ofNullable(booking.getUser());
        if (givenUser.isPresent()) {
            User u = givenUser.get();
            this.user = new UserEntity(u.getId(), u.getName(), u.getUuid());
        }
    }
}
