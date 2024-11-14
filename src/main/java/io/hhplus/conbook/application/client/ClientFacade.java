package io.hhplus.conbook.application.client;

import io.hhplus.conbook.domain.client.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClientFacade {
    private final ClientService clientService;

    public void notifyBookingHistory(ClientCommand.Notify command) {
        clientService.notifyBookingHistory(command.toBookingHistory());
    }
}
