package io.hhplus.conbook.domain.token;

import io.hhplus.conbook.domain.concert.Concert;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class TokenQueue {
    private Long id;
    private Concert concert;
    private int accessCapacity;

    public TokenQueue(Concert concert, int accessCapacity) {
        this.concert = concert;
        this.accessCapacity = accessCapacity;
    }
}
