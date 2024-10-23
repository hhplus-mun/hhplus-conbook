package io.hhplus.conbook.domain.token;

import org.springframework.security.core.AuthenticationException;

public class NotValidTokenException extends AuthenticationException {
    public NotValidTokenException(String message) {
        super(message);
    }
}
