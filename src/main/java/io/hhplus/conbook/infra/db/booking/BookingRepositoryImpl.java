package io.hhplus.conbook.infra.db.booking;

import io.hhplus.conbook.domain.booking.Booking;
import io.hhplus.conbook.domain.booking.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BookingRepositoryImpl implements BookingRepository {
    private final BookingJpaRepository bookingJpaRepository;

    @Override
    public Booking save(Booking booking) {
        return bookingJpaRepository.save(new BookingEntity(booking))
                .toDomain();
    }

    @Override
    public Booking findBy(Long id) {
        return bookingJpaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException())
                .toDomain();
    }
}
