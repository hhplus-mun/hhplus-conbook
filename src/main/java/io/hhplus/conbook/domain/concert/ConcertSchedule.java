package io.hhplus.conbook.domain.concert;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@AllArgsConstructor
@Getter
public class ConcertSchedule {
    private Long id;
    private Concert concert;
    private LocalDate concertDate;
    private int occupiedCount;

    public void audienceIncrease() {
        occupiedCount++;
    }
}
