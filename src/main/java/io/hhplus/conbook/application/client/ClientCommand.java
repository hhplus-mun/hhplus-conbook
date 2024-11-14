package io.hhplus.conbook.application.client;

import io.hhplus.conbook.domain.client.BookingHistory;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ClientCommand {
    @Builder
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
