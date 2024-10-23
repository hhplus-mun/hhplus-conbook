package io.hhplus.conbook.domain.token;

import io.hhplus.conbook.domain.concert.Concert;
import io.hhplus.conbook.domain.token.generation.*;
import io.hhplus.conbook.domain.user.User;
import io.hhplus.conbook.interfaces.api.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional(readOnly = true)
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
    @Transactional
    public Token creaateToken(User user, Concert concert) {
        TokenQueue tokenQueue = queueRepository.getTokenQueue(concert.getId());
        List<TokenQueueItem> tokenQueueItemList = queueItemRepository.itemList(concert.getId());
        if (isUserTokenExists(user.getId(), tokenQueueItemList))
            throw new IllegalStateException(ErrorCode.TOKEN_ALREADY_EXIST.getCode());

        Map<ItemStatus, List<TokenQueueItem>> groups =
                tokenQueueItemList.stream().collect(Collectors.groupingBy(TokenQueueItem::getStatus));

        List<TokenQueueItem> waitingItems = groups.getOrDefault(ItemStatus.WAITING, new ArrayList<>());
        List<TokenQueueItem> passedItems = groups.getOrDefault(ItemStatus.PASSED, new ArrayList<>());
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

    private boolean isUserTokenExists(Long id, List<TokenQueueItem> tokenList) {
        Optional<TokenQueueItem> found =
                tokenList.stream()
                        .filter(token -> token.getUser().getId().equals(id))
                        .findAny();

        return found.isPresent() ? true : false;
    }

    private boolean hasWaitingItems(TokenQueue tokenQueue, List<TokenQueueItem> passedItems) {
        return passedItems.size() >= tokenQueue.getAccessCapacity();
    }

    /**
     * 스케쥴러 참고
     */
    public boolean verifyToken(String token) {
        if (!tokenProvider.validate(token))
            throw new NotValidTokenException(ErrorCode.ACCESS_TOKEN_VALIDATION_FAILED.getCode());

        AccessPayload payload = tokenProvider.extractAccess(token);
        return queueItemRepository.existsInPass(payload.concertId(), payload.uuid());
    }

    public AccessTokenInfo parseAccessTokenInfo(String token) {
        AccessPayload payload = tokenProvider.extractAccess(token);
        return new AccessTokenInfo(payload.concertId(), payload.uuid());
    }

    public TokenStatusInfo getWaitingStatusInfo(String waitingToken) {
        if (!tokenProvider.validate(waitingToken))
            throw new NotValidTokenException(ErrorCode.WAITING_TOKEN_VALIDATION_FAILED.getCode());

        WaitPayload waitPayload = tokenProvider.extractWait(waitingToken);
        TokenQueueItem item = queueItemRepository.findItemBy(waitPayload.concertId(), waitPayload.uuid());

        TokenStatusInfo tokenInfo;
        if (item.getStatus().equals(ItemStatus.PASSED)) {
            CustomTokenClaims customTokenClaims = CustomTokenClaims.getDefaultClaims(item.getQueue().getConcert().getId(), item.getUser().getUuid());
            tokenInfo = new TokenStatusInfo(tokenProvider.generateToken(customTokenClaims));
        } else {
            tokenInfo = new TokenStatusInfo(item.getPosition());
        }

        return tokenInfo;
    }

    public List<TokenQueue> getTokenQueueListWithItems() {
        return queueRepository.getQueueListWithItems();
    }

    // TODO: for-loop를 활용해서 token을 delete하지만 bulk delete를 활용할 수 있도록 추후 리팩토링
    @Transactional
    public void removeExpiredAccessToken(List<TokenQueueItem> expiredTokens) {
        for (TokenQueueItem tokenItem : expiredTokens) {
            queueItemRepository.remove(tokenItem);
        }
    }

    // TODO: for-loop를 활용해서 token 상태를 update하지만 bulk update를 활용할 수 있도록 추후 리팩토링
    @Transactional
    public void convertToPass(List<TokenQueueItem> waitingItems, int count) {
        waitingItems.sort(Comparator.comparing(TokenQueueItem::getPosition));

        for(int i=0; i<count; i++) {
            if (waitingItems.size() == 0) return;

            TokenQueueItem token = waitingItems.remove(0);
            token.switchStatusToPass();

            queueItemRepository.updateStatus(token);
        }

        if(waitingItems.size() > 0) {
            int position = 1;
            for (TokenQueueItem waitingItem : waitingItems) {
                waitingItem.changePosition(position++);
                queueItemRepository.updateStatus(waitingItem);
            }
        }
    }

    @Transactional
    public void expireAccessRight(long concertId, String userUUID) {
        TokenQueueItem tokenItem = queueItemRepository.findItemBy(concertId, userUUID);
        tokenItem.expire();

        queueItemRepository.saveOrUpdate(tokenItem);
    }
}
