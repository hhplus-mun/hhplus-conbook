package io.hhplus.conbook.application.client;

import io.hhplus.conbook.application.event.ConcertBookingEvent;
import io.hhplus.conbook.domain.client.BookingHistory;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ClientCommand {
    public record Notify(
            Long bookingId,
            Long concertId,
            String title,
            LocalDate concertDate,
            Long seatId,
            String seatRow,
            int seatNo,
            Long userId,
            String userName,
            LocalDateTime bookingDateTime
    ) {
        public Notify(ConcertBookingEvent event) {
            this(
                event.getBookingId(),
                event.getConcertId(),
                event.getTitle(),
                event.getConcertDate(),
                event.getSeatId(),
                event.getSeatRow(),
                event.getSeatNo(),
                event.getUserId(),
                event.getUserName(),
                event.getBookingDateTime()
            );
        }

        public BookingHistory toBookingHistory() {
            return BookingHistory.builder()
                    .bookingId(bookingId)
                    .concertId(concertId)
                    .title(title)
                    .concertDate(concertDate)
                    .seatId(seatId)
                    .seatRow(seatRow)
                    .seatNo(seatNo)
                    .userId(userId)
                    .userName(userName)
                    .bookingDateTime(bookingDateTime)
                    .build();
        }
    }

    public record Update(
            long bookingId
    ) {}
}
