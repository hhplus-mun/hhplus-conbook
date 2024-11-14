package io.hhplus.conbook.interfaces.event;

import io.hhplus.conbook.application.client.ClientCommand;
import io.hhplus.conbook.application.client.ClientFacade;
import io.hhplus.conbook.application.client.ClientLogCommand;
import io.hhplus.conbook.application.client.ClientLogFacade;
import io.hhplus.conbook.application.event.ConcertBookingEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class ConcertBookingEventListener {

    private final ClientFacade clientFacade;
    private final ClientLogFacade clientLogFacade;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleConcertBooking(ConcertBookingEvent event) {
        log.info("HANDLE - {}", event);

        ClientCommand.Notify command = ClientCommand.Notify.builder()
                .bookingId(event.getBooking().getId())
                .concertId(event.getBooking().getSeat().getConcertSchedule().getConcert().getId())
                .title(event.getBooking().getSeat().getConcertSchedule().getConcert().getTitle())
                .concertDate(event.getBooking().getSeat().getConcertSchedule().getConcertDate())
                .seatId(event.getBooking().getSeat().getId())
                .seatRow(event.getBooking().getSeat().getRowName())
                .seatNo(event.getBooking().getSeat().getSeatNo())
                .userId(event.getBooking().getUser().getId())
                .userName(event.getBooking().getUser().getName())
                .bookingDateTime(event.getBooking().getCreatedAt())
                .build();

        clientFacade.notifyBookingHistory(command);
        clientLogFacade.saveNotificationHistory(new ClientLogCommand.Save(event.getBooking().getId(), LocalDateTime.now()));
    }
}
