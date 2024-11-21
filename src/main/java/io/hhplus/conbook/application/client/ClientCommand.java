package io.hhplus.conbook.application.client;

import io.hhplus.conbook.domain.booking.Booking;
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
        public Notify(Booking booking) {
            this(
                booking.getId(),
                booking.getSeat().getConcertSchedule().getConcert().getId(),
                booking.getSeat().getConcertSchedule().getConcert().getTitle(),
                booking.getSeat().getConcertSchedule().getConcertDate(),
                booking.getSeat().getId(),
                booking.getSeat().getRowName(),
                booking.getSeat().getSeatNo(),
                booking.getUser().getId(),
                booking.getUser().getName(),
                booking.getCreatedAt()
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
}
