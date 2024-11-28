package io.hhplus.conbook.domain.token;

import io.hhplus.conbook.domain.concert.Concert;
import io.hhplus.conbook.domain.token.generation.*;
import io.hhplus.conbook.domain.user.User;
import io.hhplus.conbook.interfaces.api.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional(readOnly = true)
public class TokenManager {
    private final TokenRepository tokenRepository;
    private final TokenHistoryRepository tokenHistoryRepository;
    private final TokenProvider tokenProvider;

    /**
     * 토큰을 생성
     * 구성 정보 (콘서트ID, 사용자 UUID, 대기순서) (잔여시간이 아닌 대기순서를 내려준다)
     */
    @Transactional
    public TokenInfo createToken(User user, Concert concert) {
        // to check history
        if (tokenHistoryRepository.hasValidTokenHisoryFor(concert.getId(), user.getUuid()))
            throw new IllegalStateException(ErrorCode.TOKEN_ALREADY_EXIST.getCode());

        CustomTokenClaims customTokenClaims = CustomTokenClaims.getDefaultClaims(concert.getId(), user.getUuid());
        Map<TokenStatus, Long> tokenCountsByStatus = tokenRepository.getTokenCountsByStatus(concert.getId());

        if (hasWaitingItems(tokenCountsByStatus, concert.getCapacity())) {
            customTokenClaims.changeType(TokenType.WAIT);
        }

        String jwt = tokenProvider.generateToken(customTokenClaims);

        tokenRepository.save(
                Token.builder()
                        .concertId(concert.getId())
                        .userUUID(user.getUuid())
                        .status(customTokenClaims.getType().toItemStatus())
                        .createdAt(customTokenClaims.getIssuedAt())
                        .expiredAt(customTokenClaims.getExpiredAt())
                        .build()
        );
        tokenHistoryRepository.save(new TokenHistory(concert, user.getUuid(), customTokenClaims.getType()));

        return new TokenInfo(jwt, customTokenClaims.getType());
    }

    private boolean hasWaitingItems(Map<TokenStatus, Long> statusCounts, int concertCapacity) {
        return statusCounts.getOrDefault(TokenStatus.PASSED, 0L) >= concertCapacity;
    }

    /**
     * 스케쥴러 참고
     */
    public boolean verifyToken(String token) {
        if (!tokenProvider.validate(token))
            throw new NotValidTokenException(ErrorCode.ACCESS_TOKEN_VALIDATION_FAILED.getCode());

        AccessPayload payload = tokenProvider.extractAccess(token);
        return tokenRepository.existsInPass(payload.concertId(), payload.uuid());
    }

    public AccessTokenInfo parseAccessTokenInfo(String token) {
        AccessPayload payload = tokenProvider.extractAccess(token);
        return new AccessTokenInfo(payload.concertId(), payload.uuid());
    }

    public TokenStatusInfo getWaitingStatusInfo(String waitingToken) {
        if (!tokenProvider.validate(waitingToken))
            throw new NotValidTokenException(ErrorCode.WAITING_TOKEN_VALIDATION_FAILED.getCode());

        WaitPayload waitPayload = tokenProvider.extractWait(waitingToken);
        int position = tokenRepository.findPositionFor(waitPayload.concertId(), waitingToken);

        return new TokenStatusInfo(position);
    }

    @Transactional
    public void convertToPass(long concertId, int concertCapacity) {
        Long validAccessTokenCount = tokenRepository.getTokenCountsByStatus(concertId)
                .getOrDefault(TokenStatus.PASSED, 0L);

        if (validAccessTokenCount >= concertCapacity) return;

        long availableCapacity = concertCapacity - validAccessTokenCount;
        List<String> jwts = tokenRepository.findAccessTokensOrderByCreatedAt(concertId, availableCapacity);
        List<Token> convertingTargets = new ArrayList<>();

        for (String waitingToken : jwts) {
            WaitPayload waitPayload = tokenProvider.extractWait(waitingToken);
            CustomTokenClaims claims = CustomTokenClaims.getDefaultClaims(waitPayload.concertId(), waitPayload.uuid());
            String accessToken = tokenProvider.generateToken(claims);
            Token renewalToken = new Token(concertId, claims.getUserUUID(), TokenStatus.PASSED, claims.getIssuedAt(), claims.getExpiredAt());
            renewalToken.issuedAs(accessToken);

            convertingTargets.add(renewalToken);
        }
        if (!convertingTargets.isEmpty()) tokenRepository.savePassedAll(convertingTargets);
    }

    @Transactional
    public void expireAccessRight(long concertId, String accessToken) {
        tokenRepository.remove(concertId, accessToken);
    }

    public void clearNonValidTokens(Long concertId) {
        tokenRepository.removeNonValidTokens(concertId);
    }

}
