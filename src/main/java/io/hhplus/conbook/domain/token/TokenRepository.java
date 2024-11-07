package io.hhplus.conbook.domain.token;

import java.util.List;
import java.util.Map;

public interface TokenRepository {
    void save(Token token);

    void savePassedAll(List<Token> convertingTargets);

    boolean existsInPass(long concertId, String accessToken);

    boolean existTokenFor(long queueId, String userUUID);

//    Token findAccessTokenBy(long concertId, String accessToken);

    int findPositionFor(long concertId, String waitingToken);

    void remove(long concertId, String tokenValue);

    Map<TokenStatus, Integer> getTokenCountsByStatus(long concertId);

}
