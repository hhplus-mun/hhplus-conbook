package io.hhplus.conbook.domain.concert;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Seat {
    private Long id;
    private ConcertSchedule concertSchedule;
    private boolean isOccupied;
    private String rowName;
    private int seatNo;

    public void hasReserved() {
        isOccupied = true;
    }
}
