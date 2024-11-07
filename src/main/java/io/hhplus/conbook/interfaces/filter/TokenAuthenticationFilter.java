package io.hhplus.conbook.interfaces.filter;

import io.hhplus.conbook.domain.token.AccessTokenInfo;
import io.hhplus.conbook.domain.token.TokenManager;
import io.hhplus.conbook.interfaces.api.ApiRoutes;
import io.hhplus.conbook.interfaces.api.ErrorCode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private static String JWT_AUTHORIZATION = "Authorization";
    private static String TOKEN_PREFIX = "Bearer ";

    private final TokenManager tokenManager;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();

        if (requestURI.contains(ApiRoutes.BASE_TOKEN_API_PATH)) {
            log.info("url: {}", requestURI);

            filterChain.doFilter(request, response);
            return;
        }
        String authorizationHeader = request.getHeader(JWT_AUTHORIZATION);
        log.info("token: {}, url: {}", authorizationHeader, requestURI);

        if (!StringUtils.hasText(authorizationHeader))
            throw new AccessDeniedException(ErrorCode.UNAUTHORIZED_ACCESS.getCode());

        String token = extractJwt(authorizationHeader);
        if (tokenManager.verifyToken(token)) {
            CustomAuthenticationToken authentication = new CustomAuthenticationToken();
            SecurityContextHolder.getContext().setAuthentication(authentication);

            AccessTokenInfo tokenInfo = tokenManager.parseAccessTokenInfo(token);
            if (requestURI.contains(ApiRoutes.BASE_CONCERT_API_PATH)
                    && !isValidConcertAccess(requestURI, tokenInfo.concertId()))
                throw new AccessDeniedException(ErrorCode.CONCERT_UNAUTHORIZED_ACCESS.getCode());

            request.setAttribute(CustomAttribute.JWT, token);
            request.setAttribute(CustomAttribute.CONCERT_ID, tokenInfo.concertId());
            request.setAttribute(CustomAttribute.USER_UUID, tokenInfo.uuid());
        }

        filterChain.doFilter(request, response);
    }

    private boolean isValidConcertAccess(String requestURI, long concertId) {
        String concertPath = requestURI.substring(ApiRoutes.BASE_CONCERT_API_PATH.length());
        log.info("concertPath: {}", concertPath);

        return Long.parseLong(concertPath.split("/")[1]) == concertId ? true : false;
    }

    private String extractJwt(String authorizationHeader) {
        return authorizationHeader.substring(TOKEN_PREFIX.length());
    }
}
