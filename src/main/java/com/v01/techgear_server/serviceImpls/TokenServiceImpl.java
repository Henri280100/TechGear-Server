package com.v01.techgear_server.serviceImpls;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.v01.techgear_server.dto.TokenDTO;
import com.v01.techgear_server.model.Token;
import com.v01.techgear_server.repo.TokenRepository;
import com.v01.techgear_server.service.TokenService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    @Value("${jwt.secret}")
    private String jwtSecret;
    private final TokenRepository tokenRepository;

    @Override
    public String getUserNameFromJwtToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(jwtSecret)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getSubject();
        } catch (Exception e) {
            log.error("Error extracting username from token: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(jwtSecret).build().parseClaimsJws(authToken);
            return true;
        } catch (SecurityException e) {
            log.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }

    @Override
    public boolean isTokenValid(String refreshToken) {
        try {
            Optional<Token> tokenOptional = tokenRepository.findLatestValidRefreshToken(refreshToken);
            return tokenOptional.isPresent() && !tokenOptional.get().isRevoked()
                    && !isTokenExpired(tokenOptional.get());
        } catch (Exception e) {
            log.error("Error validating token: {}", e.getMessage());
            return false;
        }
    }

    private boolean isTokenExpired(Token token) {
        return token.getExpiresAt().isBefore(Instant.now());
    }

    @Override
    @Transactional
    public void revokeToken(String token) {
        try {
            Optional<Token> tokenOpt = tokenRepository.findLatestValidRefreshToken(token);
            // If not found by refresh token, try access token
            if (tokenOpt.isEmpty()) {
                tokenOpt = tokenRepository.findLatestValidAccessToken(token);
            }

            if (tokenOpt.isPresent()) {
                Token foundToken = tokenOpt.get();
                foundToken.setRevoked(true);
                tokenRepository.save(foundToken);
                log.info("Token revoked successfully: {}", foundToken.getTokenId());
            }

        } catch (Exception e) {
            log.error("Error revoking token: {}", e.getMessage());
            throw new RuntimeException("Failed to revoke token", e);
        }
    }

    @Override
    @Transactional
    public void revokeAllUserTokens(Long userId) {
        try {
            List<Token> userTokens = tokenRepository.findAllByUser_Id(userId);
            for (Token token : userTokens) {
                if (!token.isRevoked()) {
                    token.setRevoked(true);
                    tokenRepository.save(token);
                    log.info("Token revoked: {}", token.getTokenId());
                }
            }
            log.info("All tokens revoked for user ID: {}", userId);
        } catch (Exception e) {
            log.error("Error revoking user tokens: {}", e.getMessage());
            throw new RuntimeException("Failed to revoke user tokens", e);
        }
    }

    @Override
    public Instant getRefreshTokenExpiration(String refreshToken) {
        return tokenRepository.findLatestValidRefreshToken(refreshToken)
                .map(Token::getExpiresAt)
                .orElse(null);
    }

    @Override
    public Optional<Token> findByRefreshToken(String refreshToken) {
        return tokenRepository.findLatestValidRefreshToken(refreshToken);
    }

    @Transactional
    public void revokeExpiredUserTokens(Long userId) {
        List<Token> validUserTokens = tokenRepository.findAllValidTokensByUser(userId);
        if (validUserTokens.isEmpty()) {
            return;
        }

        validUserTokens.stream()
                .filter(token -> token.getExpiresAt().isBefore(Instant.now()))
                .forEach(token -> {
                    token.setRevoked(true);
                    tokenRepository.save(token);
                    log.info("Expired token revoked for user: {}", userId);
                });
    }

    @Override
    @Transactional
    public void storeTokens(Long userId, TokenDTO tokenDTO) {
        try {
            // Revoke existing tokens first
            revokeExpiredUserTokens(userId);

            // Create new token
            Token token = new Token();
            token.setUserId(userId);
            token.setAccessToken(tokenDTO.getAccessToken());
            token.setRefreshToken(tokenDTO.getRefreshToken());
            token.setRevoked(false);
            token.setCreatedAt(Instant.now());
            token.setExpiresAt(Instant.now().plus(7, ChronoUnit.DAYS));

            tokenRepository.save(token);
            log.info("New tokens stored for user: {}", userId);
        } catch (Exception e) {
            log.error("Error storing tokens: {}", e.getMessage());
            throw new IllegalArgumentException("Failed to store tokens", e);
        }
    }

    @Scheduled(cron = "0 0 0 * * *") // Run daily at midnight
    @Transactional
    public void cleanupExpiredTokens() {
        try {
            tokenRepository.deleteExpiredAndRevokedTokens(Instant.now());
            log.info("Expired and revoked tokens cleaned up successfully");
        } catch (Exception e) {
            log.error("Error cleaning up tokens: {}", e.getMessage());
        }
    }
}
