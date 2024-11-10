package com.v01.techgear_server.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.v01.techgear_server.service.RedisLoginAttemptsService;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final UserDetailsService userDetailsService;

    private final RedisLoginAttemptsService loginAttemptService;

    @Autowired
    public CustomAuthenticationProvider(UserDetailsService userDetailsService, RedisLoginAttemptsService loginAttemptService) {
        this.userDetailsService = userDetailsService;
        this.loginAttemptService = loginAttemptService;
    }
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
       String username = authentication.getName();

        if (loginAttemptService.isAccountLocked(username)) {
            throw new LockedException("Your account is locked due to too many failed login attempts.");
        }

        UserDetails user = userDetailsService.loadUserByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        return new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

    
}

