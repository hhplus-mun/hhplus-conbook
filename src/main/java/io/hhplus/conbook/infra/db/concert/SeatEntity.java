package io.hhplus.conbook.infra.db.concert;

import io.hhplus.conbook.domain.concert.Seat;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 좌석 위치 표시 예시
 * B25 - Row B, Seat 25
 */
@Table(name = "seat")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class SeatEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seat_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concert_schedule_id")
    private ConcertScheduleEntity concertSchedule;

    private boolean isOccupied;
    private String rowName;
    private int seatNo;
    private int price;

    public SeatEntity(Seat seat) {
        this.id = seat.getId();
        this.concertSchedule = new ConcertScheduleEntity(seat.getConcertSchedule());
        this.isOccupied = seat.isOccupied();
        this.rowName = seat.getRowName();
        this.seatNo = seat.getSeatNo();
        this.price = seat.getPrice();
    }

    public Seat toDomain() {
        return new Seat(id, concertSchedule.toDomain(), isOccupied, rowName, seatNo, price);
    }

    public Seat toDomainWithoutSchedule() {
        return new Seat(id, null, isOccupied, rowName, seatNo, price);
    }
}
