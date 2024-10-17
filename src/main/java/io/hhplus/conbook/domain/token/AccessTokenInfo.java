package io.hhplus.conbook.domain.token;

public record AccessTokenInfo (
        long concertId,
        String uuid
) {}