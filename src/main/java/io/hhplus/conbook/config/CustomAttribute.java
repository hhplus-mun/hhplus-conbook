package io.hhplus.conbook.config;

/**
 * Filter에서 token을 검증한 후, <br>
 * Access 권한 정보를 HttpServletRequest attribute에 넣기위해 <br>
 * 사용하는 속성 이름
 */
public interface CustomAttribute {
    String CONCERT_ID = "tokenConcertId";
    String USER_UUID = "tokenUserUUID";
}
