package io.hhplus.conbook.interfaces.api.token;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@Tag(name = "Token management", description = "Token management API")
public interface TokenControllerApi {

    @Operation(
            summary = "대기열토큰 발급",
            description = "서비스를 이용할 토큰을 발급받는 API"
    )
    @ApiResponse(
            responseCode = "200", description = "OK",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = TokenResponse.Generate.class))
    )
    @PostMapping("/generation")
    TokenResponse.Generate generate(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "토큰생성에 필요한 정보",
                    content = @Content(schema = @Schema(
                            example = "{\"userId\": 1, \"concertId\": 1}"
                    ))
            )
            @RequestBody
            TokenRequest.Generate req
    );

    @Operation(
            summary = "대기번호를 조회하는 폴링용 API",
            description = "대기열을 통과하면 서비스를 이용할 수 있게하는 토큰을 발급합니다."
    )
    @ApiResponse(
            responseCode = "200", description = "OK",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(
                            example = "{\"status\": \"WAITING\", \"meesage\": \"현재 대기 중입니다. 대기번호: 1\", \"position\": 1, \"accessToken\": null}"
                    ))
    )
    @GetMapping("/check")
    TokenResponse.Status positionOrAccess(
            @RequestHeader(name = "WaitingToken") String token
    );
}
