package io.hhplus.conbook.domain.token;

public interface TokenHistoryRepository {
    TokenHistory save(TokenHistory tokenHistory);
    boolean hasValidTokenHisoryFor(long concertId, String userUUID);
}
