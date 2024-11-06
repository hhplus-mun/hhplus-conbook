package io.hhplus.conbook.infra.db.token;

import io.hhplus.conbook.domain.token.Token;
import io.hhplus.conbook.domain.token.TokenRepository;
import io.hhplus.conbook.domain.token.TokenStatus;
import io.hhplus.conbook.domain.token.TokenStatusCount;
import io.hhplus.conbook.interfaces.api.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class TokenRepositoryImpl implements TokenRepository {

    private final TokenQueueJpaRepository tokenQueueJpaRepository;
    private final TokenJpaRepository tokenJpaRepository;

    @Override
    public List<Token> tokenList(long concertId) {
        TokenQueueEntity tokenQueue = tokenQueueJpaRepository.findByConcertId(concertId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.QUEUE_NOT_FOUND.getCode()));

        return tokenJpaRepository.findAllByTokenQueueId(tokenQueue.getId())
                .stream()
                .map(TokenEntity::toDomain)
                .toList();
    }

    @Override
    public void save(Token token) {

        tokenJpaRepository.save(new TokenEntity(token));
    }

    @Override
    public boolean existsInPass(long concertId, String userUUID) {
        TokenEntity item =
                tokenJpaRepository.findTokenByConcertIdAndUUID(concertId, userUUID)
                        .orElseThrow(() -> new IllegalArgumentException(ErrorCode.TOKEN_NOT_FOUND.getCode()));

        return item.getStatus().equals(TokenStatus.PASSED);
    }

    @Override
    public boolean existTokenFor(long queueId, String userUUID) {

        return tokenJpaRepository.findTokenByQueueIdAndUUID(queueId, userUUID)
                .isPresent();
    }

    @Override
    public Token findTokenBy(long concertId, String userUUID) {

        return tokenJpaRepository.findTokenByConcertIdAndUUID(concertId, userUUID)
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.TOKEN_NOT_FOUND.getCode()))
                .toDomain();
    }

    @Override
    public int findPositionFor(long concertId, String userUUID) {
        return tokenJpaRepository.findTokenByConcertIdAndUUID(concertId, userUUID)
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.TOKEN_NOT_FOUND.getCode()))
                .getPosition();
    }

    @Override
    public void updateStatus(Token token) {
        tokenJpaRepository.save(new TokenEntity(token));
    }

    @Override
    public void remove(Token item) {
        tokenJpaRepository.delete(new TokenEntity(item));
    }

    @Override
    public void saveOrUpdate(Token token) {
        tokenJpaRepository.save(new TokenEntity(token));
    }

    @Override
    public Map<TokenStatus, Integer> getTokenCountsByStatus(long concertId) {

        return tokenJpaRepository.findTokenStatusCountBy(concertId)
                .stream()
                .collect(Collectors.toMap(
                        TokenStatusCount::getStatus,
                        TokenStatusCount::getCount
                ));
    }
}