package io.hhplus.conbook.interfaces.filter;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Collections;

/**
 * Empty
 */
public class CustomAuthenticationToken extends AbstractAuthenticationToken {

    /**
     * 인증을 위한 Blank AuthenticationToken
     */
    public CustomAuthenticationToken() {
        this(Collections.singleton(new SimpleGrantedAuthority("ROLE_ACCESS_USER")));
    }

    /**
     * Creates a token with the supplied array of authorities.
     *
     * @param authorities the collection of <tt>GrantedAuthority</tt>s for the principal
     *                    represented by this authentication object.
     */
    public CustomAuthenticationToken(Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return null;
    }
}
