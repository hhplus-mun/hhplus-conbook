package io.hhplus.conbook.domain.token;

import io.hhplus.conbook.domain.concert.Concert;
import io.hhplus.conbook.domain.token.generation.*;
import io.hhplus.conbook.domain.user.User;
import io.hhplus.conbook.interfaces.api.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional(readOnly = true)
public class TokenManager {

    private final TokenQueueRepository queueRepository;
    private final TokenRepository tokenRepository;
    private final TokenProvider tokenProvider;

    /**
     * 토큰을 생성
     * 구성 정보 (콘서트ID, 사용자 UUID, 대기순서) (잔여시간이 아닌 대기순서를 내려준다)
     */
    @Transactional
    public TokenInfo creaateToken(User user, Concert concert) {
        TokenQueue tokenQueue = queueRepository.getTokenQueue(concert.getId());

        if (tokenRepository.existTokenFor(tokenQueue.getId(), user.getUuid()))
            throw new IllegalStateException(ErrorCode.TOKEN_ALREADY_EXIST.getCode());

        CustomTokenClaims customTokenClaims = CustomTokenClaims.getDefaultClaims(concert.getId(), user.getUuid());
        Map<TokenStatus, Integer> statusCounts = tokenRepository.getTokenCountsByStatus(concert.getId());

        if (hasWaitingItems(tokenQueue, statusCounts)) {
            customTokenClaims.changeType(TokenType.WAIT);
            customTokenClaims.addPosition(statusCounts.getOrDefault(TokenStatus.WAITING, 0) + 1);
        }

        String jwt = tokenProvider.generateToken(customTokenClaims);
        tokenRepository.save(
                Token.builder()
                        .queue(tokenQueue)
                        .userUUID(user.getUuid())
                        .status(customTokenClaims.getType().toItemStatus())
                        .position(customTokenClaims.getPosition())
                        .createdAt(customTokenClaims.getIssuedAt())
                        .expiredAt(customTokenClaims.getExpiredAt())
                        .build()
        );

        return new TokenInfo(jwt, customTokenClaims.getType());
    }

    private boolean hasWaitingItems(TokenQueue tokenQueue, Map<TokenStatus, Integer> statusCounts) {
        return statusCounts.getOrDefault(TokenStatus.PASSED, 0) >= tokenQueue.getAccessCapacity();
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
        int position = tokenRepository.findPositionFor(waitPayload.concertId(), waitPayload.uuid());

        return new TokenStatusInfo(position);
    }

    public List<TokenQueue> getTokenQueueListWithItems() {
        return queueRepository.getQueueListWithTokens();
    }

    // TODO: for-loop를 활용해서 token을 delete하지만 bulk delete를 활용할 수 있도록 추후 리팩토링
    @Transactional
    public void removeExpiredAccessToken(List<Token> expiredTokens) {
        for (Token tokenItem : expiredTokens) {
            tokenRepository.remove(tokenItem);
        }
    }

    // TODO: for-loop를 활용해서 token 상태를 update하지만 bulk update를 활용할 수 있도록 추후 리팩토링
    @Transactional
    public void convertToPass(List<Token> waitingItems, int count) {
        waitingItems.sort(Comparator.comparing(Token::getPosition));

        for(int i=0; i<count; i++) {
            if (waitingItems.isEmpty()) return;

            Token token = waitingItems.remove(0);
            token.switchStatusToPass();

            tokenRepository.updateStatus(token);
        }

        if(!waitingItems.isEmpty()) {
            int position = 1;
            for (Token waitingItem : waitingItems) {
                waitingItem.changePosition(position++);
                tokenRepository.updateStatus(waitingItem);
            }
        }
    }

    // TODO: expire 처리 후 DB상에서는 유효성이 사라지나 filter에서는 아직 사용가능 -> 만료처리할 수 있도록 수정
    @Transactional
    public void expireAccessRight(long concertId, String userUUID) {
        Token tokenItem = tokenRepository.findTokenBy(concertId, userUUID);
        tokenItem.expire();

        tokenRepository.saveOrUpdate(tokenItem);
    }
}
