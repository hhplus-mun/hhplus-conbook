package io.hhplus.conbook.domain.booking;

public class AlreadyOccupiedException extends RuntimeException {
    public AlreadyOccupiedException(String message) {
        super(message);
    }
}
