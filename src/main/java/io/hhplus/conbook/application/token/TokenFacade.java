package io.hhplus.conbook.application.token;

import io.hhplus.conbook.domain.concert.Concert;
import io.hhplus.conbook.domain.concert.ConcertService;
import io.hhplus.conbook.domain.token.TokenInfo;
import io.hhplus.conbook.domain.token.TokenStatusInfo;
import io.hhplus.conbook.domain.token.TokenManager;
import io.hhplus.conbook.domain.user.User;
import io.hhplus.conbook.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TokenFacade {

    private final UserService userService;
    private final ConcertService concertService;
    private final TokenManager tokenManager;

    public TokenResult.Access getAccessToken(TokenCommand.Access command) {
        User user = userService.getUser(command.userId());
        Concert concert = concertService.getConcert(command.concertId());
        TokenInfo token = tokenManager.createToken(user, concert);

        return new TokenResult.Access(token.jwt(), token.type());
    }

    public TokenResult.Check checkStatus(TokenCommand.Check check) {
        TokenStatusInfo statusInfo = tokenManager.getWaitingStatusInfo(check.waitingToken());

        return new TokenResult.Check(statusInfo);
    }
}
