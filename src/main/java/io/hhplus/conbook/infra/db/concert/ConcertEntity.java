package io.hhplus.conbook.infra.db.concert;

import io.hhplus.conbook.domain.concert.Concert;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "concert")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
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

    public ConcertEntity(Concert concert) {
        this.id = concert.getId();
        this.title = concert.getTitle();
        this.artist = concert.getArtist();
        this.place = concert.getPlace();
    }
}
