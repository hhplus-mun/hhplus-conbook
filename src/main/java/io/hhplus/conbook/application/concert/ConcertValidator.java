package io.hhplus.conbook.application.concert;

import io.hhplus.conbook.interfaces.api.ErrorCode;
import org.springframework.security.access.AccessDeniedException;


public class ConcertValidator {

    /**
     * 서비스 권한 대상인 콘서트인지 아닌지 확인. <br>
     * {@link AccessDeniedException}
     *
     * @param requestedId
     * @param parsedConcertId
     */
    public static void validate(Long requestedId, long parsedConcertId) {
        if (!requestedId.equals(parsedConcertId)) throw new AccessDeniedException(ErrorCode.CONCERT_UNAUTHORIZED_ACCESS.getCode());
    }

}
