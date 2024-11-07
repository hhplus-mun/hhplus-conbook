package io.hhplus.conbook.performance.token.infra.db;

import io.hhplus.conbook.domain.token.Token;
import io.hhplus.conbook.domain.token.TokenRepository;
import io.hhplus.conbook.domain.token.TokenStatus;
import io.hhplus.conbook.domain.token.TokenStatusCount;
import io.hhplus.conbook.interfaces.api.ErrorCode;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TokenRepositoryImpl implements TokenRepository {

    private final TokenJpaRepository tokenJpaRepository;

    // --- Constructor

    public TokenRepositoryImpl(TokenJpaRepository tokenJpaRepository) {
        this.tokenJpaRepository = tokenJpaRepository;
    }

    // --- Method

    @Override
    public void save(Token token) {
        tokenJpaRepository.save(new TokenEntity(token));
    }

    @Override
    public void savePassedAll(List<Token> tokenList) {
        tokenJpaRepository.saveAll(tokenList.stream().map(TokenEntity::new).toList());
    }

    @Override
    public boolean existsInPass(long concertId, String accessToken) {
        TokenEntity item =
                tokenJpaRepository.findTokenByConcertIdAndTokenValue(concertId, accessToken)
                        .orElseThrow(() -> new IllegalArgumentException(ErrorCode.TOKEN_NOT_FOUND.getCode()));

        return item.getStatus().equals(TokenStatus.PASSED);
    }

    @Override
    public int findPositionFor(long concertId, String waitingToken) {
        return tokenJpaRepository.findWaitingTokenList(concertId)
                .indexOf(waitingToken);
    }

    @Override
    public void remove(long concertId, String tokenValue) {
        tokenJpaRepository.deleteBy(concertId, tokenValue);
    }

    @Override
    public Map<TokenStatus, Long> getTokenCountsByStatus(long concertId) {

        return tokenJpaRepository.findTokenStatusCountBy(concertId)
                .stream()
                .collect(Collectors.toMap(
                        TokenStatusCount::getStatus,
                        TokenStatusCount::getCount
                ));
    }

    @Override
    public List<String> findAccessTokensOrderByCreatedAt(long concertId, long availableCapacity) {

        return tokenJpaRepository.findAllByConcertId(concertId)
                .stream()
                .limit(availableCapacity)
                .toList();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void removeNonValidTokens(Long concertId) {
        tokenJpaRepository.removeNonValidTokenBy(concertId, TokenStatus.PASSED);
        tokenJpaRepository.removeNonValidTokenBy(concertId, TokenStatus.WAITING);
    }
}
