package io.hhplus.conbook.interfaces.api;

public record ErrorResponse(
        String code,
        String message
) {
}
