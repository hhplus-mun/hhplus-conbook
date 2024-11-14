package io.hhplus.conbook.application.client;

import java.time.LocalDateTime;

public class ClientLogCommand {
    public record Save (
            long bookingId,
            LocalDateTime finishedAt
    ) {}
}
