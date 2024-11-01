package com.v01.techgear_server.serviceImpls;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.v01.techgear_server.exception.TokenNotFoundException;
import com.v01.techgear_server.model.Token;
import com.v01.techgear_server.repo.TokenRepository;
import com.v01.techgear_server.service.TokenService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TokenServiceImpl implements TokenService {

    @Autowired
    private TokenRepository tokenRepository;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Override
    public String getUserNameFromJwtToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(jwtSecret)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    @Override
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(jwtSecret).build().parseClaimsJws(authToken);
            return true;
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("Expired JWT token: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is not supported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }

    @Override
    public boolean isTokenValid(String refreshToken) {
        // Find the token by its refresh token string
        Optional<Token> tokenOptional = tokenRepository.findByRefreshToken(refreshToken);

        // If no token is found, it's invalid
        if (tokenOptional.isEmpty()) {
            return false; // No tokens found for the refresh token
        }

        // Check if the token is revoked
        Token token = tokenOptional.get();
        return !token.isRevoked(); // Return true if the token is not revoked
    }

    @Override
    @Transactional
    public void revokeToken(Long id) {
        tokenRepository.findById(id)
                .ifPresentOrElse(
                        token -> {
                            token.setRevoked(true);
                            tokenRepository.save(token);
                        },
                        () -> {
                            throw new TokenNotFoundException("No token found for id: " + id);
                        });
    }

    @Override
    @Transactional
    public void revokeAllUserTokens(Long id) {
        List<Token> userTokens = tokenRepository.findAllByUser_Id(id);
        userTokens.forEach(token -> token.setRevoked(true));
        tokenRepository.saveAll(userTokens);
    }

    @Override
    public Instant getRefreshTokenExpiration(String refreshToken) {
        // Logic to retrieve the expiration time of the refresh token
        // This could involve querying a database or checking a cache
        Optional<Token> tokenOptional = tokenRepository.findByRefreshToken(refreshToken);
        return tokenOptional.map(Token::getExpiresAt).orElse(null);
    }

    @Override
    public void storeTokens(Long userId, String accessToken, String refreshToken) {
        Token token = new Token();
        token.setUser_id(userId);
        token.setAccessToken(accessToken);
        token.setRefreshToken(refreshToken);
        token.setRevoked(false); // Set to false initially
        token.setCreatedAt(Instant.now());
        token.setExpiresAt(Instant.now().plusSeconds(5 * 60)); // Set expiration for access token (5 minutes)

        // Save the token to the database

        tokenRepository.save(token);

    }

}
