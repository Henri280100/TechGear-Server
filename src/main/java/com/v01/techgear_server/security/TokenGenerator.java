package com.v01.techgear_server.security;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import com.v01.techgear_server.dto.TokenDTO;
import com.v01.techgear_server.exception.InvalidTokenException;
import com.v01.techgear_server.model.Token;
import com.v01.techgear_server.model.User;
import com.v01.techgear_server.repo.TokenRepository;
import com.v01.techgear_server.repo.UserRepository;

import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenGenerator {
    private final JwtEncoder accessTokenEncoder;
    private final JwtEncoder refreshTokenEncoder;
    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;

    private static final String ISSUER = "techgear_server";
    private static final long ACCESS_TOKEN_EXPIRY_MINUTES = 15;
    private static final long REFRESH_TOKEN_EXPIRY_DAYS = 7;

    //Store

    public TokenDTO generateTokens(Authentication authentication) {
        try {
            Token token = createToken(authentication);
            token = tokenRepository.save(token);

            log.info("Generated new tokens for user: {}. Expires at: {}",
                    token.getUserId(), token.getExpiresAt());

            return TokenDTO.builder()
                    .accessToken(token.getAccessToken())
                    .refreshToken(token.getRefreshToken())
                    .tokenType("Bearer")
                    .expiresIn(ACCESS_TOKEN_EXPIRY_MINUTES * 60)
                    .build();
        } catch (Exception e) {
            log.error("Error generating tokens: ", e);
            throw e;
        }
    }

    private Token createToken(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Instant now = Instant.now();

        String accessToken = createAccessToken(authentication, now);
        String refreshToken = createRefreshToken(authentication, now);

        Token token = new Token();

        token.setUserId(user.getUserId());

        token.setAccessToken(accessToken);

        token.setRefreshToken(refreshToken);

        token.setRevoked(false);

        token.setCreatedAt(now);

        token.setExpiresAt(now.plus(REFRESH_TOKEN_EXPIRY_DAYS, ChronoUnit.DAYS));
        return token;
    }

    private String createAccessToken(Authentication authentication, Instant now) {
        User user = (User) authentication.getPrincipal();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(ISSUER)
                .issuedAt(now)
                .expiresAt(now.plus(ACCESS_TOKEN_EXPIRY_MINUTES, ChronoUnit.MINUTES))
                .subject(user.getUserId().toString())
                .claim("type", "access_token")
                .claim("authorities", authentication.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList())
                .build();

        return accessTokenEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    private String createRefreshToken(Authentication authentication, Instant now) {
        User user = (User) authentication.getPrincipal();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(ISSUER)
                .issuedAt(now)
                .expiresAt(now.plus(REFRESH_TOKEN_EXPIRY_DAYS, ChronoUnit.DAYS))
                .subject(user.getUserId().toString())
                .claim("type", "refresh_token")
                .build();

        return refreshTokenEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    public TokenDTO refreshToken(String refreshToken) {
        try {
            Token token = tokenRepository.findLatestValidRefreshToken(refreshToken)
                    .orElseThrow(() -> {
                        log.warn("Refresh token not found or revoked: {}", refreshToken);
                        return new InvalidTokenException("Refresh token not found or revoked");
                    });

            if (token.getExpiresAt().isBefore(Instant.now())) {
                log.warn("Refresh token expired for user: {}", token.getUserId());
                throw new InvalidTokenException("Refresh token has expired");
            }

            log.info("Found valid refresh token for user: {}. Created at: {}, Expires at: {}",
                    token.getUserId(), token.getCreatedAt(), token.getExpiresAt());

            User user = userRepository.findById(token.getUserId())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    user, null, user.getAuthorities());

            // Generate new tokens
            Token newToken = createToken(authentication);

            // Revoke old token
            token.setRevoked(true);
            tokenRepository.save(token);

            // Save new token
            newToken = tokenRepository.save(newToken);

            return TokenDTO.builder()
                    .accessToken(newToken.getAccessToken())
                    .refreshToken(newToken.getRefreshToken())
                    .tokenType("Bearer")
                    .expiresIn(ACCESS_TOKEN_EXPIRY_MINUTES * 60)
                    .build();

        } catch (JwtException e) {
            throw new InvalidTokenException("Invalid refresh token");
        }
    }
}
