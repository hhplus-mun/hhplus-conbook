package io.hhplus.conbook.interfaces.api.concert;

import io.hhplus.conbook.domain.booking.BookingStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;

@RestController
@RequestMapping("/v1/concerts")
public class ConcertController {

    /**
     * TODO: 콘서트 예약가능 날짜 API
     */
    @GetMapping("/{id}/available-dates")
    public ConcertResponse.AvailableDates availableDates(
            @PathVariable Long id
    ) {
        return new ConcertResponse.AvailableDates(
                1L,
                "Cold Play",
                Arrays.asList(new ConcertResponse.ConcertDetails(LocalDate.now(), 50))
        );
    }

    /**
     * TODO: 콘서트 예약가능 좌석 API
     */
    @GetMapping("/{id}/dates/{date}/available-seats")
    public ConcertResponse.AvailableSeats availableSeats(
            @PathVariable Long id,
            @PathVariable String date
    ) {
        return new ConcertResponse.AvailableSeats(
                1L,
                "Cold Play",
                LocalDate.now(),
                Arrays.asList(new ConcertResponse.SeatDto(1L, "A", 1))
        );
    }

    /**
     * TODO: 좌석 예약 요청 API
     */
    @PostMapping("/{id}/dates/{date}/booking")
    public ConcertResponse.Booking bookSeat(
            @PathVariable Long id,
            @PathVariable String date,
            @RequestBody Long seatId
    ) {
        return new ConcertResponse.Booking(
                1L,
                1L,
                LocalDate.now(),
                1L,
                BookingStatus.RESERVED,
                LocalDateTime.now().plusMinutes(5)
        );
    }
}
