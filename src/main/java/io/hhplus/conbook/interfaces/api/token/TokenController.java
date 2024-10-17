package io.hhplus.conbook.interfaces.api.token;

import io.hhplus.conbook.application.token.TokenCommand;
import io.hhplus.conbook.application.token.TokenFacade;
import io.hhplus.conbook.application.token.TokenResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/token")
@RequiredArgsConstructor
@Slf4j
public class TokenController implements TokenControllerApi {

    private final TokenFacade tokenFacade;

    /**
     * 유저 토큰 발급 API
     */
    @Override
    @PostMapping("/generation")
    public TokenResponse.Generate generate(
            @RequestBody TokenRequest.Generate req
    ) {
        TokenResult.Access token = tokenFacade.getAccessToken(new TokenCommand.Access(req.userId(), req.concertId()));

        return new TokenResponse.Generate(token.jwt(), token.type());
    }

    /**
     * 대기번호 조회
     * 사용자 대기열 통과 시 액세스 토큰을 발급
     *
     * @param token - custom HTTP Header 대기열 토큰을 Header에 넣어야 한다.
     * @return 대기번호 or 액세스 토큰
     */
    @Override
    @GetMapping("/check")
    public TokenResponse.Status positionOrAccess(
            @RequestHeader(name = "WaitingToken") String token
    ) {
        log.info("\nwaitingToken: {}", token);

        TokenResult.Check checkResult = tokenFacade.checkStatus(new TokenCommand.Check(token));

        return new TokenResponse.Status(checkResult.tokenStatusInfo());
    }
}
