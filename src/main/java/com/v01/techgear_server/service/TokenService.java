package com.v01.techgear_server.service;

import java.time.Instant;

public interface TokenService {
    boolean isTokenValid(String refreshToken);
    void revokeToken(Long token);
    void revokeAllUserTokens(Long user_id);
    Instant getRefreshTokenExpiration(String refreshToken);
    void storeTokens(Long userId, String accessToken, String refreshToken);

    boolean validateJwtToken(String authToken);
    String getUserNameFromJwtToken(String token);
}
