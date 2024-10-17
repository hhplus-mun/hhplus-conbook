package io.hhplus.conbook.infra.db.concert;

import io.hhplus.conbook.domain.concert.Concert;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Table(name = "concert")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ConcertEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "concert_id")
    private Long id;
    private String title;
    private String artist;
    private String place;

    public Concert toDomain() {
        return new Concert(id, title, artist, place);
    }

    public ConcertEntity(Long id, String title, String artist, String place) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.place = place;
    }
}
