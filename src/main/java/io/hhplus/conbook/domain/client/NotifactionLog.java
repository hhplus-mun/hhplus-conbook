package io.hhplus.conbook.domain.client;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class NotifactionLog {
    private Long bookingId;
    private LocalDateTime finishedAt;
    private LocalDateTime paidAt;

    public void switchToPaid() {
        paidAt = LocalDateTime.now();
    }
}
