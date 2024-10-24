package io.hhplus.conbook.interfaces.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.hhplus.conbook.interfaces.api.ApiRoutes;
import io.hhplus.conbook.interfaces.api.ErrorCode;
import io.hhplus.conbook.interfaces.api.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 현재 ApiControllerAdvice에서 handleNotValidTokenException 를 먼저 호출한다.
 * TokenAuthenticationFilter 진입 후 Controller에 접근하기 전
 * 로직에서 발생하는 예외는 모두 토큰의 인증과 관련되어있다.
 *
 */
@Component
@Slf4j
public class TokenAuthenticationEntryPointHandler implements AuthenticationEntryPoint {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        HttpStatus statusCode = HttpStatus.UNAUTHORIZED;
        ErrorCode errorCode = getErrorCode(request);
        ResponseEntity<ErrorResponse> errorResponse = ResponseEntity.status(statusCode)
                .body(new ErrorResponse(errorCode.getCode(), errorCode.getDesc()));

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(statusCode.value());

        objectMapper.writeValue(response.getOutputStream(), errorResponse);
    }

    private ErrorCode getErrorCode(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        System.out.println("entryPointHandler requestURI"+ requestURI);

        if (requestURI.contains(ApiRoutes.BASE_TOKEN_API_PATH)) {
            return ErrorCode.WAITING_TOKEN_VALIDATION_FAILED;
        }

        return ErrorCode.ACCESS_TOKEN_VALIDATION_FAILED;
    }
}
