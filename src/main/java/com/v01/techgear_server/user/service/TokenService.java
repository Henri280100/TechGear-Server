package com.v01.techgear_server.user.service;

import java.time.Instant;
import java.util.Optional;

import com.v01.techgear_server.user.dto.TokenDTO;
import com.v01.techgear_server.user.model.Token;

public interface TokenService {

    String getUserNameFromJwtToken(String token);
    boolean validateJwtToken(String authToken);
    boolean isTokenValid(String token);
    void revokeToken(String token);
    void revokeAllUserTokens(Long userId);
    Instant getRefreshTokenExpiration(String refreshToken);
    Optional<Token> findByRefreshToken(String refreshToken);
    void storeTokens(Long userId, TokenDTO tokenDTO);
    void cleanupExpiredTokens();
}
