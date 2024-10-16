package io.hhplus.conbook.domain.token.generation;

public interface CustomClaims {
    String CONCERT = "concertId";
    String UUID = "uuid";
    String TOKEN_TYPE = "type";
    String POSITION = "position";

    int EXPIRATION_MIN = 10;
}
