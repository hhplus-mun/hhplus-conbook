package io.hhplus.conbook.infra.db.token;

import io.hhplus.conbook.domain.token.TokenHistory;
import io.hhplus.conbook.domain.token.TokenHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
@RequiredArgsConstructor
public class TokenHistoryRepositoryImpl implements TokenHistoryRepository {

    private final TokenHistoryJpaRepository historyJpaRepository;

    @Override
    public TokenHistory save(TokenHistory tokenHistory) {
        return historyJpaRepository.save(new TokenHistoryEntity(tokenHistory))
                .toDomain();
    }

    @Override
    public boolean hasValidTokenHisoryFor(long concertId, String userUUID) {
        return historyJpaRepository.findHistoryBy(concertId, userUUID)
                .stream()
                .anyMatch(h -> h.getExpiresAt().isAfter(LocalDateTime.now()));
    }
}
