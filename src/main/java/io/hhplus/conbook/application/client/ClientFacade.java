package io.hhplus.conbook.application.client;

import io.hhplus.conbook.domain.client.ClientService;
import io.hhplus.conbook.domain.client.NotifactionLog;
import io.hhplus.conbook.domain.client.NotificationLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClientFacade {
    private final ClientService clientService;
    private final NotificationLogRepository notificationLogRepository;

    public void notifyBookingHistory(ClientCommand.Notify command) {
        clientService.notifyBookingHistory(command.toBookingHistory());
    }

    public void notifyBookingHistoryUpdate(ClientCommand.Update command) {
        NotifactionLog notification = notificationLogRepository.findByBookingId(command.bookingId());
        notification.switchToPaid();

        notificationLogRepository.save(notification);
    }
}
