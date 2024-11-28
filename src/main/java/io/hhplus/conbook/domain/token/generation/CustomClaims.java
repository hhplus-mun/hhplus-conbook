package io.hhplus.conbook.domain.token.generation;

public interface CustomClaims {
    String CONCERT = "concertId";
    String UUID = "uuid";
    String TOKEN_TYPE = "type";

//    long ACCESS_EXPIRATION_MIN = 15;
    long ACCESS_EXPIRATION_MIN = 7;
//    long WAITING_EXPIRATION_MIN = 10;
    long WAITING_EXPIRATION_MIN = 5;
}
