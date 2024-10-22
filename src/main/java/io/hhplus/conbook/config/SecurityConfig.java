package io.hhplus.conbook.config;

import io.hhplus.conbook.domain.token.TokenManager;
import io.hhplus.conbook.interfaces.api.ApiRoutes;
import io.hhplus.conbook.interfaces.filter.TokenAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final TokenManager tokenManager;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .httpBasic(basic -> basic.disable())
                .formLogin(formLogin -> formLogin.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(request -> request
                        .requestMatchers(ApiRoutes.BASE_TOKEN_API_PATH + "/**").permitAll()
                        .requestMatchers(ApiRoutes.API_V1 + "/**").authenticated()
                )
                .addFilterBefore(new TokenAuthenticationFilter(tokenManager), UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
