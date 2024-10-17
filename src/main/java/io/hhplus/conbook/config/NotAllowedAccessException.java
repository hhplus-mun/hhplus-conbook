package io.hhplus.conbook.config;

public class NotAllowedAccessException extends RuntimeException{
    public NotAllowedAccessException(String message) {
        super(message);
    }
}
