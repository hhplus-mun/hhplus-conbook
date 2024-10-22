package io.hhplus.conbook.domain.point;

public class NotValidRequestException extends RuntimeException {
    public NotValidRequestException(String message) {
        super(message);
    }
}
