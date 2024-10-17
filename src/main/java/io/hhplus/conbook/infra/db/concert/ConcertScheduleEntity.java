package io.hhplus.conbook.infra.db.concert;

import io.hhplus.conbook.domain.concert.ConcertSchedule;
import jakarta.persistence.*;

import java.time.LocalDate;

@Table(name = "concert_schedule")
@Entity
public class ConcertScheduleEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "concert_schedule_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concert_id")
    private ConcertEntity concert;

    private LocalDate concertDate;

    private int occupiedCount;
    private int capacity;

    public ConcertSchedule toDomain() {
        return new ConcertSchedule(id, concert.toDomain(), concertDate, occupiedCount, capacity);
    }
}
