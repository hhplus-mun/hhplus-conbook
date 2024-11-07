package io.hhplus.conbook.infra.redis.token;

import io.hhplus.conbook.domain.token.Token;
import io.hhplus.conbook.domain.token.TokenRepository;
import io.hhplus.conbook.domain.token.TokenStatus;
import io.hhplus.conbook.domain.token.generation.CustomTokenClaims;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@Primary
@RequiredArgsConstructor
public class TokenRedisRepositoryImpl implements TokenRepository {
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public void save(Token token) {
        String key = getConcertQueueKey(token.getConcertId(), token.getStatus());

        redisTemplate.opsForZSet()
                .add(key, token.getUserUUID(), System.currentTimeMillis());
    }

    @Override
    public void savePassedAll(List<Token> tokens) {
        String key = getConcertQueueKey(tokens.get(0).getConcertId(), TokenStatus.PASSED);

        Set<ZSetOperations.TypedTuple<String>> tuples = tokens.stream()
                .map(Token::getTokenValue)
                .map(t -> new DefaultTypedTuple<>(t, (double) System.currentTimeMillis()))
                .collect(Collectors.toSet());

        redisTemplate.opsForZSet().add(key, tuples);
    }

    private String getConcertQueueKey(long concertId, TokenStatus status) {

        return String.format("concert:%d:queue:%s", concertId, status).toLowerCase();
    }

    @Override
    public boolean existsInPass(long concertId, String accessToken) {
        String key = getConcertQueueKey(concertId, TokenStatus.PASSED);

        Double score = redisTemplate.opsForZSet()
                .score(key, accessToken);

        return Optional.ofNullable(score).isPresent();
    }

    @Override
    public int findPositionFor(long concertId, String waitingToken) {
        String key = getConcertQueueKey(concertId, TokenStatus.WAITING);

        return redisTemplate.opsForZSet()
                .rank(key, waitingToken)
                .intValue();
    }

    @Override
    public void remove(long concertId, String tokenValue) {
        String key = getConcertQueueKey(concertId, TokenStatus.PASSED);

        redisTemplate.opsForZSet().remove(key, tokenValue);
    }

    @Override
    public Map<TokenStatus, Long> getTokenCountsByStatus(long concertId) {
        Map<TokenStatus, Long> tokenCounts = new HashMap<>();

        String passedKey = getConcertQueueKey(concertId, TokenStatus.PASSED);
        String waitingKey = getConcertQueueKey(concertId, TokenStatus.WAITING);

        tokenCounts.put(TokenStatus.PASSED, redisTemplate.opsForZSet().size(passedKey));
        tokenCounts.put(TokenStatus.WAITING, redisTemplate.opsForZSet().size(waitingKey));

        return tokenCounts;
    }

    @Override
    public List<String> findAccessTokensOrderByCreatedAt(long concertId, long availableCapacity) {
        String key = getConcertQueueKey(concertId, TokenStatus.PASSED);

        return redisTemplate.opsForZSet().popMin(key, availableCapacity)
                .stream()
                .map(ZSetOperations.TypedTuple::getValue)
                .toList();
    }

    @Override
    public void removeNonValidTokens(Long concertId) {
        String passedKey = getConcertQueueKey(concertId, TokenStatus.PASSED);
        String waitingKey = getConcertQueueKey(concertId, TokenStatus.WAITING);

        long now = System.currentTimeMillis();
        redisTemplate.opsForZSet().removeRange(passedKey, 0, now - (CustomTokenClaims.ACCESS_EXPIRATION_MIN * 60 * 1000));
        redisTemplate.opsForZSet().removeRange(waitingKey, 0, now - (CustomTokenClaims.WAITING_EXPIRATION_MIN * 60 * 1000));
    }

}
