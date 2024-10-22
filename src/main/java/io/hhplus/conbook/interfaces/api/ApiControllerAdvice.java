package io.hhplus.conbook.interfaces.api;

import io.hhplus.conbook.domain.booking.AlreadyOccupiedException;
import io.hhplus.conbook.domain.point.NotValidRequestException;
import io.hhplus.conbook.domain.token.NotValidTokenException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


@Slf4j
@RestControllerAdvice
public class ApiControllerAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = NotValidTokenException.class)
    public ResponseEntity<ErrorResponse> handleNotValidTokenException(NotValidTokenException e) {
        HttpStatus statusCode = HttpStatus.UNAUTHORIZED;
        log.error(statusCode.getReasonPhrase(), e);

        ErrorCode errorCode = ErrorCode.resolve(e.getMessage());
        return ResponseEntity.status(statusCode)
                .body(new ErrorResponse(errorCode.getCode(), errorCode.getDesc()));
    }

    @ExceptionHandler(value = NotValidRequestException.class)
    public ResponseEntity<ErrorResponse> handleNotValidRequestException(NotValidRequestException e) {
        HttpStatus statusCode = HttpStatus.CONFLICT;
        log.error(statusCode.getReasonPhrase(), e);

        ErrorCode errorCode = ErrorCode.resolve(e.getMessage());
        return ResponseEntity.status(statusCode)
                .body(new ErrorResponse(errorCode.getCode(), errorCode.getDesc()));
    }

    @ExceptionHandler(value = AlreadyOccupiedException.class)
    public ResponseEntity<ErrorResponse> handleAlreadyOccupiedException(AlreadyOccupiedException e) {
        HttpStatus statusCode = HttpStatus.CONFLICT;
        log.error(statusCode.getReasonPhrase(), e);

        ErrorCode errorCode = ErrorCode.resolve(e.getMessage());
        return ResponseEntity.status(statusCode)
                .body(new ErrorResponse(errorCode.getCode(), errorCode.getDesc()));
    }

    @ExceptionHandler(value = AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException e) {
        HttpStatus statusCode = HttpStatus.FORBIDDEN;
        log.error(statusCode.getReasonPhrase(), e);

        ErrorCode errorCode = ErrorCode.resolve(e.getMessage());
        return ResponseEntity.status(statusCode)
                .body(new ErrorResponse(errorCode.getCode(), errorCode.getDesc()));
    }

    @ExceptionHandler(value = IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalStateException(IllegalStateException e) {
        HttpStatus statusCode = HttpStatus.INTERNAL_SERVER_ERROR;
        log.error(statusCode.getReasonPhrase(), e);

        ErrorCode errorCode = ErrorCode.resolve(e.getMessage());
        return ResponseEntity.status(statusCode)
                .body(new ErrorResponse(errorCode.getCode(), errorCode.getDesc()));
    }

    @ExceptionHandler(value = IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        HttpStatus statusCode = HttpStatus.NOT_FOUND;
        log.error(statusCode.getReasonPhrase(), e);

        ErrorCode errorCode = ErrorCode.resolve(e.getMessage());
        return ResponseEntity.status(statusCode)
                .body(new ErrorResponse(errorCode.getCode(), errorCode.getDesc()));
    }
    
    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException e) {
        HttpStatus statusCode = HttpStatus.INTERNAL_SERVER_ERROR;
        log.error(statusCode.getReasonPhrase(), e);

        return ResponseEntity.status(statusCode)
                .body(new ErrorResponse(String.valueOf(statusCode.value()), statusCode.getReasonPhrase()));
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        HttpStatus statusCode = HttpStatus.INTERNAL_SERVER_ERROR;
        log.error(statusCode.getReasonPhrase(), e);

        return ResponseEntity.status(statusCode)
                .body(new ErrorResponse(String.valueOf(statusCode.value()), statusCode.getReasonPhrase()));
    }
}
