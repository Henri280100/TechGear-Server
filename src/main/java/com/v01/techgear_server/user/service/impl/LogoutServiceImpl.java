package com.v01.techgear_server.user.service.impl;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import com.v01.techgear_server.user.service.TokenService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LogoutServiceImpl implements LogoutHandler {

    private final TokenService tokenService;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }
        jwt = authHeader.substring(7);
        tokenService.revokeToken(jwt);
        SecurityContextHolder.clearContext();
    }

}
