package io.hhplus.conbook.domain.concert;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ConcertService {
    private final ConcertRepository concertRepository;
    private final ConcertScheduleRepository scheduleRepository;
    private final SeatRepository seatRepository;

    public Concert getConcert(long id) {
        return concertRepository.getConcertBy(id);
    }

    public List<ConcertSchedule> getConcertScheduleList(long concertId) {
        return concertRepository.findScheduleListBy(concertId);
    }

    public List<Seat> getConcertSeatList(long concertId, String date) {
        LocalDate localDate = convertToLocalDate(date);
        ConcertSchedule schedule = scheduleRepository.findScheduleBy(concertId, localDate);

        return seatRepository.findAvailableListBy(schedule.getId());
    }

    public ConcertSchedule getConcertSchedule(long concertId, String date) {
        LocalDate localDate = convertToLocalDate(date);
        return scheduleRepository.findScheduleBy(concertId, localDate);
    }

    private LocalDate convertToLocalDate(String date) {
        LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyyMMdd"));
        return localDate;
    }
}