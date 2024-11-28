package io.hhplus.conbook.application.event;

import io.hhplus.conbook.domain.booking.Booking;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public class ConcertBookingEvent {
    private Long bookingId;
    private Long concertId;
    private String title;
    private LocalDate concertDate;
    private Long seatId;
    private String seatRow;
    private int seatNo;
    private Long userId;
    private String userName;
    private LocalDateTime bookingDateTime;

    public ConcertBookingEvent(Booking b) {
        this.bookingId = b.getId();
        this.concertId = b.getSeat().getConcertSchedule().getConcert().getId();
        this.title = b.getSeat().getConcertSchedule().getConcert().getTitle();
        this.concertDate = b.getSeat().getConcertSchedule().getConcertDate();
        this.seatId = b.getSeat().getId();
        this.seatRow = b.getSeat().getRowName();
        this.seatNo = b.getSeat().getSeatNo();
        this.userId = b.getUser().getId();
        this.userName = b.getUser().getName();
        this.bookingDateTime = b.getCreatedAt();
    }
}