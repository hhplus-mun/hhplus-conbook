package io.hhplus.conbook.domain.token.generation;

public record WaitPayload(
        long concertId,
        String uuid,
        int position
) {}
