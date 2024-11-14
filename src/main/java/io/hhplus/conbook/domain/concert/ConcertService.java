package io.hhplus.conbook.domain.concert;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConcertService {
    private final ConcertRepository concertRepository;
    private final ConcertScheduleRepository scheduleRepository;
    private final SeatRepository seatRepository;

    public Concert getConcert(long id) {
        return concertRepository.getConcertBy(id);
    }

    public List<Long> getConcertIds() {
        return concertRepository.getConcertList()
                .stream()
                .map(Concert::getId)
                .toList();
    }

    public List<ConcertSchedule> getAvailableConcertScheduleList(long concertId) {
        return scheduleRepository.findScheduleListBy(concertId)
                .stream()
                .filter(s -> s.getCapacity() > s.getOccupiedCount())
                .toList();
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
        return LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyyMMdd"));
    }

    @Transactional
    public void updateScheduleStatus(long concertId, String date) {
        LocalDate localDate = convertToLocalDate(date);
        ConcertSchedule schedule = scheduleRepository.findScheduleBy(concertId, localDate);
        schedule.audienceIncrease();

        scheduleRepository.saveOrUpdate(schedule);
    }
}
