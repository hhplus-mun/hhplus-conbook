package io.hhplus.conbook.application.client;

import io.hhplus.conbook.domain.client.ClientLogService;
import io.hhplus.conbook.domain.client.NotifactionLog;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClientLogFacade {
    private final ClientLogService clientLogService;

    public void saveNotificationHistory(ClientLogCommand.Save command) {
        clientLogService.saveHistory(new NotifactionLog(command.bookingId(), command.finishedAt(), null));
    }
}
