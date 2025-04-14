package com.v01.techgear_server.security;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.v01.techgear_server.user.service.RedisLoginAttemptsService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OAuth2LoginFailureHandler implements AuthenticationFailureHandler {

    private final RedisLoginAttemptsService loginAttemptService;



    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {
        String username = request.getParameter("username");

        if (username != null) {
            loginAttemptService.loginFailed(username); // Log the failed attempt
        }

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("Login failed: " + exception.getMessage());
    }

}
