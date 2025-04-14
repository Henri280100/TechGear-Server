package com.v01.techgear_server.security;

import java.io.IOException;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.v01.techgear_server.exception.UserNotFoundException;
import com.v01.techgear_server.user.model.User;
import com.v01.techgear_server.user.repository.UserRepository;
import com.v01.techgear_server.user.service.RedisLoginAttemptsService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final RedisLoginAttemptsService loginAttemptsService;

    public OAuth2LoginSuccessHandler(UserRepository userRepository, RedisLoginAttemptsService loginAttemptsService) {
        this.userRepository = userRepository;
        this.loginAttemptsService = loginAttemptsService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String email = oAuth2User.getAttribute("email");

        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            throw new UserNotFoundException("User not found with email: " + email);
        }


        loginAttemptsService.loginSucceeded(email);

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);
        response.setCharacterEncoding("UTF-8");

        response.getWriter().write("Login Success");
    }

}
