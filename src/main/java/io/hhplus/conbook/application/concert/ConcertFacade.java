package io.hhplus.conbook.application.concert;

import io.hhplus.conbook.domain.booking.Booking;
import io.hhplus.conbook.domain.booking.BookingService;
import io.hhplus.conbook.domain.concert.ConcertSchedule;
import io.hhplus.conbook.domain.concert.ConcertService;
import io.hhplus.conbook.domain.concert.Seat;
import io.hhplus.conbook.domain.user.User;
import io.hhplus.conbook.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConcertFacade {
    private final ConcertService concertService;
    private final UserService userService;
    private final BookingService bookingService;

    public List<ConcertResult.Search> availableDates(ConcertCommand.Search search) {

        return concertService.getConcertScheduleList(search.concertId())
                .stream()
                .filter(s -> s.getCapacity() > s.getOccupiedCount())
                .map(s -> new ConcertResult.Search(
                        search.concertId(),
                        s.getConcert().getTitle(),
                        s.getConcertDate(),
                        s.getOccupiedCount(),
                        s.getCapacity())
                )
                .toList();
    }

    public ConcertResult.Status availableSeats(ConcertCommand.Status status) {

        List<Seat> seatList = concertService.getConcertSeatList(status.concertId(), status.date());
        ConcertSchedule schedule = seatList.get(0).getConcertSchedule();

        return new ConcertResult.Status(
                schedule.getConcert().getId(),
                schedule.getConcert().getTitle(),
                schedule.getConcertDate(),
                seatList.stream()
                        .map(s -> new ConcertResult.SeatInfo(
                                s.getId(), s.getRowName(), s.getSeatNo()
                        )).toList()
        );
    }

    /**
     * 각 공연은 모두 좌석을 생성한다고 가정.
     * - 좌석을 미리 안 만들 경우 어떤 식으로 처리할 지는 추후 여유가 있을 때 구현
     *  (좌석의 좌표값(x,y)만 제공될 경우)
     */
    public ConcertResult.BookingDto bookConcertSeat(ConcertCommand.Booking booking) {
        User user = userService.getUserByUUID(booking.userUUID());

        // scheduleId, seat 정보,
        Booking bookingResult = bookingService.createBooking(booking.seatId(), user);
        bookingService.addSchedule(bookingResult);

        concertService.updateSeatStatus(booking.concertId(), booking.date());

        return new ConcertResult.BookingDto(
                bookingResult.getId(),
                user.getName(),
                bookingResult.getSeat().getRowName(),
                bookingResult.getSeat().getSeatNo(),
                bookingResult.getCreatedAt(),
                bookingResult.getExpiredAt()
        );
    }
}
