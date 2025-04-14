package com.v01.techgear_server.security;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import com.v01.techgear_server.user.model.User;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JWTtoUserConvertor implements Converter<Jwt, UsernamePasswordAuthenticationToken> {
    private final UserDetailsService userDetailsService;

    public JWTtoUserConvertor(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    public UsernamePasswordAuthenticationToken convert(Jwt jwt) {

        if (jwt == null) {
            throw new AuthenticationException("JWT cannot be null") {
                @Override
                public String getMessage() {
                    return super.getMessage();
                }
            };
        }
        User user = (User) userDetailsService.loadUserByUsername(jwt.getSubject());
        Collection<String> roles = extractAuthoritiesFromJwt(jwt);
        Collection<? extends GrantedAuthority> authorities = roles.stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
        return new UsernamePasswordAuthenticationToken(user, jwt, authorities);
    }

    // Helper method to extract authorities from the JWT
    private Collection<String> extractAuthoritiesFromJwt(Jwt jwt) {
        Map<String, Object> claims = jwt.getClaims();
        Object authoritiesClaim = claims.getOrDefault("authorities", Collections.emptyList());

        if (authoritiesClaim instanceof Collection<?> collection) {
            return collection.stream()
                    .map(Object::toString)
                    .toList();
        } else {
            // Handle cases where "authorities" is not a collection (log a warning?)
            return Collections.emptyList();
        }
    }

}
