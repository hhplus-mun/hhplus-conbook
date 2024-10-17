package io.hhplus.conbook.domain.token;

import io.hhplus.conbook.domain.concert.Concert;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Getter
public class TokenQueue {
    private Long id;
    private Concert concert;
    private int accessCapacity;
    private List<TokenQueueItem> queueItems = new ArrayList<>();

    public TokenQueue(Concert concert, int accessCapacity) {
        this.concert = concert;
        this.accessCapacity = accessCapacity;
    }

    public TokenQueue(Long id, Concert concert, int accessCapacity) {
        this.id = id;
        this.concert = concert;
        this.accessCapacity = accessCapacity;
    }
}
