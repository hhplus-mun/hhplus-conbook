package io.hhplus.conbook.interfaces.api.concert;

import io.hhplus.conbook.application.concert.ConcertCommand;
import io.hhplus.conbook.application.concert.ConcertFacade;
import io.hhplus.conbook.application.concert.ConcertResult;
import io.hhplus.conbook.interfaces.api.ApiRoutes;
import io.hhplus.conbook.interfaces.filter.CustomAttribute;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping(ApiRoutes.BASE_CONCERT_API_PATH)
@RequiredArgsConstructor
public class ConcertController implements ConcertControllerApi {

    private final ConcertFacade concertFacade;

    /**
     * 콘서트 예약가능 날짜 API
     *
     * @param id concertId
     */
    @Override
    @GetMapping("/{id}/available-dates")
    public ConcertResponse.AvailableDates availableDates(
            @PathVariable Long id
    ) {
        List<ConcertResult.Search> searchResults = concertFacade.availableDates(new ConcertCommand.Search(id));

        return new ConcertResponse.AvailableDates(
                searchResults.get(0).concertId(),
                searchResults.get(0).title(),
                searchResults.stream()
                        .map(r -> new ConcertResponse.ConcertDetails(r.date(), r.capacity()))
                        .toList()
        );
    }

    /**
     * 콘서트 예약가능 좌석 API
     *
     * @param id concertId
     * @param date yyyyMMdd (queryString)
     */
    @Override
    @GetMapping("/{id}/available-seats")
    public ConcertResponse.AvailableSeats availableSeats(
            @PathVariable Long id,
            @RequestParam String date
    ) {
        ConcertResult.Status seatStatus = concertFacade.availableSeats(new ConcertCommand.Status(id, date));

        return new ConcertResponse.AvailableSeats(
                seatStatus.concertId(),
                seatStatus.concertTitle(),
                seatStatus.date(),
                seatStatus.seatInfo().stream()
                        .map(s -> new ConcertResponse.SeatDetails(s.id(), s.rowName(), s.seatNo()))
                        .toList()
        );
    }

    /**
     * 좌석 예약 요청 API
     */
    @Override
    @PostMapping("/{id}/booking")
    public ConcertResponse.Booking bookSeat(
            @PathVariable long id,
            @RequestBody ConcertRequest.Booking req,
            @RequestAttribute(name = CustomAttribute.USER_UUID) String userUUID
    ) {
        ConcertResult.BookingSeat result = concertFacade.bookConcertSeat(new ConcertCommand.Booking(id, req.date(), userUUID, req.seatId()));

        return new ConcertResponse.Booking(
                result.bookingId(),
                result.userName(),
                result.rowName(),
                result.seatNo(),
                result.bookingDateTime(),
                result.expirationTime()
        );
    }
}
