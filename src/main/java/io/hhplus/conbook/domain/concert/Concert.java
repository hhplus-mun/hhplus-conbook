package io.hhplus.conbook.domain.concert;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Concert {
    private Long id;
    private String title;
    private String artist;
    private String place;

    public Concert(String title, String artist, String place) {
        this.title = title;
        this.artist = artist;
        this.place = place;
    }
}
