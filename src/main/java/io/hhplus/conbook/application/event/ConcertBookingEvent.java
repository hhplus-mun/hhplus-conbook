package io.hhplus.conbook.application.event;

import io.hhplus.conbook.domain.booking.Booking;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ConcertBookingEvent {

//    private Long bookingId;
//    private Long concertId;
//    private String title;
//    private LocalDate concertDate;
//    private Long seatId;
//    private String seatRow;
//    private int seatNo;
//    private Long userId;
//    private String userName;
//    private LocalDateTime bookingDateTime;
    private Booking booking;
}