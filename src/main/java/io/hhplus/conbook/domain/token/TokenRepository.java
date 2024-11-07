package io.hhplus.conbook.domain.token;

import java.util.List;
import java.util.Map;

public interface TokenRepository {
    void save(Token token);

    void savePassedAll(List<Token> convertingTargets);

    boolean existsInPass(long concertId, String accessToken);

    int findPositionFor(long concertId, String waitingToken);

    void remove(long concertId, String tokenValue);

    Map<TokenStatus, Long> getTokenCountsByStatus(long concertId);

    List<String> findAccessTokensOrderByCreatedAt(long concertId, long availableCapacity);

    void removeNonValidTokens(Long concertId);
}
