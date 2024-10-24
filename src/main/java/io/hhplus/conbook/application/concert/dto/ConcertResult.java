package io.hhplus.conbook.application.concert.dto;

import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

public class ConcertResult {
    @Builder
    public record Search(
            long concertId,
            String title,
            LocalDate date,
            int soldCount,
            int capacity
    ) {}

    public record Status(
            long concertId,
            String concertTitle,
            LocalDate date,
            List<SeatInfo> seatInfo
    ) {}

    @Builder
    public record SeatInfo (
            long id,
            String rowName,
            int seatNo
    ) {}

}
