package io.hhplus.conbook.infra.db.booking;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BookingJpaRepositoryTest {

    @Autowired
    BookingJpaRepository bookingJpaRepository;

    @Test
    @DisplayName("[정상]: JPA 쿼리확인 - 예약정보 조회 확인")
    void jpaQueryDisplayBooking() {
        // given
        long id = 1;

        bookingJpaRepository.findById(id);
    }
}