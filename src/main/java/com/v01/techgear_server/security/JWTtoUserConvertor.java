package com.v01.techgear_server.security;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import com.v01.techgear_server.model.User;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JWTtoUserConvertor implements Converter<Jwt, UsernamePasswordAuthenticationToken> {

    @Override
    public UsernamePasswordAuthenticationToken convert(Jwt jwt) {
        if (jwt == null) {
            return null;
        }

        User user = new User();
        try {
            user.setUserId(Long.valueOf(jwt.getSubject()));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid user ID in JWT", e);
        }

        // Extract roles from "authorities" claim (common in OAuth2)
        Collection<String> roles = extractAuthoritiesFromJwt(jwt);
        log.info("Roles: {}", roles);
        // Convert roles to GrantedAuthority objects
        Collection<? extends GrantedAuthority> authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.toUpperCase()))
                .toList();

        log.info("Authorize: {}", authorities);
        // Create the Authentication object with roles/authorities
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
