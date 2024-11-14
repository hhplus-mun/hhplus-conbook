package io.hhplus.conbook.domain.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClientLogService {
    private final NotificationLogRepository notificationLogRepository;

    @Transactional
    public void saveHistory(NotifactionLog notifactionLog) {
        notificationLogRepository.save(notifactionLog);
    }
}
