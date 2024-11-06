package io.hhplus.conbook.domain.token;

import java.util.List;
import java.util.Map;

public interface TokenRepository {
    List<Token> tokenList(long concertId);

    void pushItem(Token token);

    boolean existsInPass(long concertId, String userUUID);

    boolean existTokenFor(long queueId, String userUUID);

    Token findTokenBy(long concertId, String userUUID);

    void updateStatus(Token token);

    void remove(Token tokenItem);

    void saveOrUpdate(Token token);

    Map<TokenStatus, Integer> getTokenCountsByStatus(long concertId);
}
