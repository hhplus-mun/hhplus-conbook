package io.hhplus.conbook.interfaces.api.concert;

import io.hhplus.conbook.config.CustomAttribute;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Concert management", description = "Concert management API")
public interface ConcertControllerApi {

    @Operation(
            summary = "예약가능한 날짜",
            description = "예약가능한 날짜를 조회할 수 있습니다."
    )
    @GetMapping("/{id}/available-dates")
    ConcertResponse.AvailableDates availableDates(
            @PathVariable Long id,
            @RequestAttribute(name = CustomAttribute.CONCERT_ID) long concertId
    );

    @Operation(
            summary = "예약가능한 좌석정보",
            description = "예약가능한 좌석정보를 조회할 수 있습니다."
    )
    @GetMapping("/{id}/available-seats")
    ConcertResponse.AvailableSeats availableSeats(
            @PathVariable Long id,
            @RequestParam String date,
            @RequestAttribute(name = CustomAttribute.CONCERT_ID) long concertId
    );

    @Operation(
            summary = "좌석 예약 요청",
            description = "날짜와 좌석정보를 입력받아 좌석을 예약합니다."
    )
    @PostMapping("/{id}/booking")
    ConcertResponse.Booking bookSeat(
            @PathVariable long id,
            @RequestBody ConcertRequest.Booking req,
            @RequestAttribute(name = CustomAttribute.CONCERT_ID) long concertId,
            @RequestAttribute(name = CustomAttribute.USER_UUID) String userUUID
    );
}
