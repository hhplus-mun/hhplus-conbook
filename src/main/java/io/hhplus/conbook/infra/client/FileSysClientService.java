package io.hhplus.conbook.infra.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.hhplus.conbook.domain.client.BookingHistory;
import io.hhplus.conbook.domain.client.ClientService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
public class FileSysClientService implements ClientService {
    protected static final String CONTEXT_PATH = "./history";
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    void init() {
        objectMapper.registerModule(new JavaTimeModule())
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    @Override
    public void notifyBookingHistory(BookingHistory bookingHistory) {
        try {
            File historyFile = getHistoryFileOrDefault();
            String historyJson = objectMapper.writeValueAsString(bookingHistory) + "\n";
            Files.write(historyFile.toPath(), historyJson.getBytes(),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);

        } catch (IOException e) {
            log.error(e.toString());
        }
    }

    private File getHistoryFileOrDefault() throws IOException {
        String month = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMM"));
        String today = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        log.debug("month: {}, today: {}", month, today);

        File historyDir = new File(CONTEXT_PATH + File.separator + month);
        if (!historyDir.exists()) historyDir.mkdirs();

        File historyFile = new File(historyDir.getPath() + File.separator + today);
        if (!historyFile.exists()) historyFile.createNewFile();

        return historyFile;
    }
}
