package io.hhplus.conbook.domain.booking;

public interface BookingRepository {
    Booking save(Booking booking);

    Booking findBy(Long id);
}
