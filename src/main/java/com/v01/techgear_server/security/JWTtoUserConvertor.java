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

    @SuppressWarnings("unchecked")
    @Override
    @Nullable
    public UsernamePasswordAuthenticationToken convert(@SuppressWarnings("null") Jwt source) {
        User user = new User();
        user.setId(Long.valueOf(source.getId()));
        return new UsernamePasswordAuthenticationToken(user, source, Collections.EMPTY_LIST);
    }

}
