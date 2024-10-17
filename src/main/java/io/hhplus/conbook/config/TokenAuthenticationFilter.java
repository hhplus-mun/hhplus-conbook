package io.hhplus.conbook.config;

import io.hhplus.conbook.domain.token.AccessTokenInfo;
import io.hhplus.conbook.domain.token.TokenManager;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
        String authorizationHeader = request.getHeader(JWT_AUTHORIZATION);
        String requestURI = request.getRequestURI();
        log.info("token: {}, url: {}", authorizationHeader, requestURI);

        if (!StringUtils.hasText(authorizationHeader))
            throw new NotAllowedAccessException();

        String token = extractJwt(authorizationHeader);
        if (tokenManager.verifyToken(token)) {
            CustomAuthenticationToken authentication = new CustomAuthenticationToken();
            SecurityContextHolder.getContext().setAuthentication(authentication);

            AccessTokenInfo tokenInfo = tokenManager.parseAccessTokenInfo(token);
            request.setAttribute(CustomAttribute.CONCERT_ID, tokenInfo.concertId());
            request.setAttribute(CustomAttribute.USER_UUID, tokenInfo.uuid());
        }

        filterChain.doFilter(request, response);
    }

    private String extractJwt(String authorizationHeader) {
        return authorizationHeader.substring(TOKEN_PREFIX.length());
    }
}
