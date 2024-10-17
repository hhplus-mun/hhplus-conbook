package io.hhplus.conbook.interfaces.api.token;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

public interface TokenControllerApi {
    @PostMapping("/generation")
    TokenResponse.Generate generate(
            @RequestBody TokenRequest.Generate req
    );

    @GetMapping("/check")
    TokenResponse.Status positionOrAccess(
            @RequestHeader(name = "WaitingToken") String token
    );
}
