package io.hhplus.conbook.infra.client;

import io.hhplus.conbook.domain.client.BookingHistory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class FileSysClientServiceTest {
    @Autowired
    FileSysClientService fileSysClientService;

    @BeforeEach
    void clear() {
        System.out.println("=== CLEAR ===");
        String month = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMM"));
        File historyDir = new File(FileSysClientService.CONTEXT_PATH + File.separator + month);

        if (historyDir.exists()) {
            for (File file : historyDir.listFiles()) {
                file.delete();
            }
            historyDir.delete();
        }
    }

    @Test
    @DisplayName("[정상]: 예약정보를 파일시스템에 파일로 생성 (외부 API의 기능으로 가정)")
    void saveBookingHistoryFromService() {
        // given
        BookingHistory history = BookingHistory.builder()
                .bookingId(1L)
                .concertId(1L)
                .title("[TEST]: concert")
                .concertDate(LocalDate.now().minusDays(10))
                .seatId(1L)
                .seatRow("A")
                .seatNo(1)
                .userId(1L)
                .userName("james")
                .bookingDateTime(LocalDateTime.now())
                .build();

        // when
        fileSysClientService.notifyBookingHistory(history);

        // then
        String month = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMM"));
        File historyDir = new File(FileSysClientService.CONTEXT_PATH + File.separator + month);

        assertThat(historyDir.exists()).isTrue();
    }
}