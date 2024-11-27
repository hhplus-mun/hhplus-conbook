package io.hhplus.conbook.application.concert;

import io.hhplus.conbook.application.concert.dto.ConcertCommand;
import io.hhplus.conbook.application.concert.dto.ConcertResult;
import io.hhplus.conbook.domain.concert.ConcertSchedule;
import io.hhplus.conbook.domain.concert.ConcertService;
import io.hhplus.conbook.domain.concert.Seat;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConcertFacade {
    private final ConcertService concertService;

    @Cacheable(value = "concertSchedules", key = "#serach.concertId")
    public List<ConcertResult.Search> availableDates(ConcertCommand.Search search) {
        return concertService.getAvailableConcertScheduleList(search.concertId())
                .stream()
                .map(s -> ConcertResult.Search.builder()
                        .concertId(search.concertId())
                        .title(s.getConcert().getTitle())
                        .date(s.getConcertDate())
                        .soldCount(s.getOccupiedCount())
                        .capacity(s.getConcert().getCapacity())
                        .build()
                )
                .toList();
    }

    public ConcertResult.Status availableSeats(ConcertCommand.Status status) {

        List<Seat> seatList = concertService.getConcertSeatList(status.concertId(), status.date());
        ConcertSchedule schedule = seatList.get(0).getConcertSchedule();

        return new ConcertResult.Status(
                schedule.getConcert().getId(),
                schedule.getConcert().getTitle(),
                schedule.getConcertDate(),
                seatList.stream()
                        .map(s -> ConcertResult.SeatInfo.builder()
                                .id(s.getId())
                                .rowName(s.getRowName())
                                .seatNo(s.getSeatNo())
                                .build())
                        .toList()
        );
    }
}
