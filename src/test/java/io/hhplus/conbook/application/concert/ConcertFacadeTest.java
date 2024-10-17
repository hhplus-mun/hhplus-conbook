package io.hhplus.conbook.application.concert;

import io.hhplus.conbook.domain.user.User;
import io.hhplus.conbook.domain.user.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ConcertFacadeTest {

    @Autowired
    ConcertFacade concertFacade;
    @Autowired
    UserService userService;

    @Test
    @DisplayName("[정상]: 콘서트 예약 유즈케이스")
    void bookingConcert() {
        // given
        // sql 디렉토리에 아래 내용 정부 저장되어있음
        long concertId = 1;
        String date = "20241002";
        String uuid = "f47ac10b-58cc-4372-a567-0e02b2c3d479";
        long seatId = 1L;
        User userByUUID = userService.getUserByUUID(uuid);

        // when
        ConcertResult.BookingDto bookingDto = concertFacade.bookConcertSeat(new ConcertCommand.Booking(concertId, date, uuid, seatId));

        // then
        assertThat(bookingDto.userName()).isEqualTo(userByUUID.getName());
    }
}