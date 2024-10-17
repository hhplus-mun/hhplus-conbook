package io.hhplus.conbook.application.concert;

public class ConcertValidator {

    /**
     * 서비스 권한 대상인 콘서트인지 아닌지 확인. <br>
     * {@link UnauthorizedConcertAccessException}
     *
     * @param requestedId
     * @param parsedConcertId
     */
    public static void validate(Long requestedId, long parsedConcertId) {
        if (!requestedId.equals(parsedConcertId)) throw new UnauthorizedConcertAccessException();
    }

}
