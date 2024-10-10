package io.hhplus.conbook.interfaces.api.token;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/token")
public class TokenController {

    /**
     * TODO: 유저 토큰 발급 API
     * @param userId > 0
     * @return
     */
    @PostMapping("/generation")
    public TokenResponse.Generate generate(
            @RequestBody Long userId
    ) {
        String prefix = "Bearer ";
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";

        return new TokenResponse.Generate(prefix + token);
    }

    /**
     * TODO: 대기번호 조회
     */
    @GetMapping("/check")
    public TokenResponse.Position position() {
        return new TokenResponse.Position(1);
    }
}
