package io.hhplus.conbook.domain.token;

import io.hhplus.conbook.domain.concert.Concert;
import io.hhplus.conbook.domain.token.generation.*;
import io.hhplus.conbook.domain.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class TokenManager {

    private final TokenQueueRepository queueRepository;
    private final TokenQueueItemRepository queueItemRepository;
    private final TokenProvider tokenProvider;

    /**
     * 토큰을 생성
     * 구성 정보 (콘서트ID, 사용자 UUID, 대기순서) (잔여시간이 아닌 대기순서를 내려준다)
     *
     * @param user
     * @param concert
     * @return
     */
    public Token creaateToken(User user, Concert concert) {
        TokenQueue tokenQueue = queueRepository.getTokenQueue(concert.getId());
        Map<ItemStatus, List<TokenQueueItem>> groups = queueItemRepository.itemList(concert.getId())
                .stream()
                .collect(Collectors.groupingBy(TokenQueueItem::getStatus));

        List<TokenQueueItem> waitingItems = groups.get(ItemStatus.WAITING);
        List<TokenQueueItem> passedItems = groups.get(ItemStatus.PASSED);
        CustomTokenClaims customTokenClaims = CustomTokenClaims.getDefaultClaims(concert.getId(), user.getUuid());

        if (hasWaitingItems(tokenQueue, passedItems)) {
            customTokenClaims.changeType(TokenType.WAIT);
            customTokenClaims.addPosition(waitingItems.size() + 1);
        }

        String jwt = tokenProvider.generateToken(customTokenClaims);
        queueItemRepository.pushItem(
                TokenQueueItem.builder()
                        .queue(tokenQueue)
                        .user(user)
                        .status(customTokenClaims.getType().toItemStatus())
                        .position(customTokenClaims.getPosition())
                        .createdAt(customTokenClaims.getIssuedAt())
                        .expiredAt(customTokenClaims.getExpiredAt())
                        .build()
        );

        return new Token(jwt, customTokenClaims.getType());
    }

    private boolean hasWaitingItems(TokenQueue tokenQueue, List<TokenQueueItem> passedItems) {
        return passedItems.size() >= tokenQueue.getAccessCapacity();
    }

    /**
     * 스케쥴러 참고
     */
    public boolean verifyToken(String token) {
        if (!tokenProvider.validate(token))
            throw new NotValidTokenException(String.format("JWT validation fail - [TokenType]: %s", TokenType.ACCESS));

        AccessPayload payload = tokenProvider.extractAccess(token);
        return queueItemRepository.existsInPass(payload.concertId(), payload.uuid());
    }

    public AccessTokenInfo parseAccessTokenInfo(String token) {
        AccessPayload payload = tokenProvider.extractAccess(token);
        return new AccessTokenInfo(payload.concertId(), payload.uuid());
    }

    public TokenStatusInfo getWaitingStatusInfo(String waitingToken) {
        if (!tokenProvider.validate(waitingToken))
            throw new NotValidTokenException(String.format("JWT validation fail - [TokenType]: %s", TokenType.WAIT));

        WaitPayload waitPayload = tokenProvider.extractWait(waitingToken);
        TokenQueueItem item = queueItemRepository.getItemBy(waitPayload.concertId(), waitPayload.uuid());

        TokenStatusInfo tokenInfo;
        if (item.getStatus().equals(ItemStatus.PASSED)) {
            CustomTokenClaims customTokenClaims = CustomTokenClaims.getDefaultClaims(item.getQueue().getConcert().getId(), item.getUser().getUuid());
            tokenInfo = new TokenStatusInfo(tokenProvider.generateToken(customTokenClaims));
        } else {
            tokenInfo = new TokenStatusInfo(item.getPosition());
        }

        return tokenInfo;
    }
}
