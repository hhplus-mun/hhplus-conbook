package io.hhplus.conbook.infra.db.token;

import io.hhplus.conbook.domain.token.Token;
import io.hhplus.conbook.domain.token.TokenQueue;
import io.hhplus.conbook.infra.db.concert.ConcertEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Table(name = "token_queue")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class TokenQueueEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "token_queue_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concert_id")
    private ConcertEntity concert;
    private int accessCapacity;

    @OneToMany(mappedBy = "tokenQueue", cascade = CascadeType.ALL)
    List<TokenEntity> tokens = new ArrayList<>();

    public TokenQueueEntity(TokenQueue tokenQueue) {
        this.id = tokenQueue.getId();
        this.accessCapacity = tokenQueue.getAccessCapacity();
        this.concert = new ConcertEntity(tokenQueue.getConcert());
    }

    public TokenQueue toDomainWithoutTokens() {
        return new TokenQueue(id, concert.toDomain(), accessCapacity);
    }

    public TokenQueue toDomainWithTokens() {
        TokenQueue tokenQueue = new TokenQueue(id, concert.toDomain(), accessCapacity);
        List<Token> tokenList =
                tokens.stream().map(TokenEntity::toDomain).toList();
        tokenQueue.getTokens().addAll(tokenList);

        return tokenQueue;
    }
}
