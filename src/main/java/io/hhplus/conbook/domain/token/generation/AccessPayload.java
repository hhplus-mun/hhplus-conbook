package io.hhplus.conbook.domain.token.generation;

import java.time.LocalDateTime;

/**
 * Access Token Payload
 */
public record AccessPayload(
        long concertId,
        String uuid,
        LocalDateTime expiredAt
) {}