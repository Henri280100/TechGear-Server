package com.v01.techgear_server.security;

import java.util.Collections;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import com.v01.techgear_server.model.User;

@Component
public class JWTtoUserConvertor implements Converter<Jwt, UsernamePasswordAuthenticationToken> {

    @Override
    public UsernamePasswordAuthenticationToken convert( Jwt source) {
        if (source == null) {
            return null; // Handle null JWT case
        }

        User user = new User();
        

            try {
                user.setId(Long.valueOf(source.getSubject()));
            } catch (NumberFormatException e) {
                // Log error or handle exception as necessary
                throw new IllegalArgumentException("Please check again", e);
            }

        return new UsernamePasswordAuthenticationToken(user, source, Collections.emptyList());
    }

}
