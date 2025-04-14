package com.v01.techgear_server.security;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import com.v01.techgear_server.user.dto.TokenDTO;
import com.v01.techgear_server.user.mapping.TokenMapper;
import com.v01.techgear_server.user.model.Token;
import com.v01.techgear_server.user.model.User;
import com.v01.techgear_server.user.repository.TokenRepository;
import com.v01.techgear_server.enums.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenGenerator {
    private final TokenMapper tokenMapper;
    @Qualifier("jwtAccessTokenEncoder")
    private final JwtEncoder accessTokenEncoder;
    @Qualifier("jwtRefreshTokenEncoder")
    private final JwtEncoder refreshTokenEncoder;
    private final TokenRepository tokenRepository;

    private static final String ISSUER = "techgear_server";
    private static final long ACCESS_TOKEN_EXPIRY_MINUTES = 15;
    private static final long REFRESH_TOKEN_EXPIRY_DAYS = 7;

    public TokenDTO generateTokens(User user) {
        try {
            Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
            Token token = createAndSaveToken(authentication, user);
            log.info("Generated tokens for user: {}. Expires at: {}", token.getUserId(), token.getExpiresDate());
            return buildTokenDTO(token);
        } catch (DataAccessException e) {
            log.error("Database error generating tokens: ", e);
            throw new RuntimeException("Failed to save token", e);
        } catch (Exception e) {
            log.error("Unexpected error generating tokens: ", e);
            throw e;
        }
    }

    private Token createAndSaveToken(Authentication authentication, User user) {
        Token token = createToken(authentication, user);
        return tokenRepository.save(token);
    }

    private Token createToken(Authentication authentication, User user) {
        Instant now = Instant.now();

        String accessToken = createAccessToken(authentication, user, now);
        String refreshToken = createRefreshToken(user, now);

        TokenDTO tokenDTO = TokenDTO.builder()
                .clientId(user.getUserId())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .revoked(false)
                .createdDate(now)
                .expiresDate(now.plus(REFRESH_TOKEN_EXPIRY_DAYS, ChronoUnit.DAYS))
                .build();

        return tokenMapper.toEntity(tokenDTO);
    }

    private String createAccessToken(Authentication authentication, User user, Instant now) {
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(ISSUER)
                .issuedAt(now)
                .expiresAt(now.plus(ACCESS_TOKEN_EXPIRY_MINUTES, ChronoUnit.MINUTES))
                .subject(user.getUserId()
                        .toString())
                .claim("type", "access_token")
                .claim("authorities", authentication.getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList())
                .build();

        return accessTokenEncoder.encode(JwtEncoderParameters.from(claims))
                .getTokenValue();
    }

    private void revokeOldRefreshTokens(Long userId) {
        List<Token> oldTokens = tokenRepository.findByUserIdAndRevokedFalse(userId);
        if (oldTokens.isEmpty()) {
            log.error("No refresh tokens found for user: {}", userId);
            return;
        }
        oldTokens.forEach(token -> {
            token.setRevoked(true);
            tokenRepository.save(token);
        });
    }

    private String createRefreshToken(User user, Instant now) {
        revokeOldRefreshTokens(user.getUserId());
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(ISSUER)
                .issuedAt(now)
                .expiresAt(now.plus(REFRESH_TOKEN_EXPIRY_DAYS, ChronoUnit.DAYS))
                .subject(user.getUserId().toString())
                .claim("type", "refresh_token")
                .build();
        return refreshTokenEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }


    private TokenDTO buildTokenDTO(Token token) {
        return TokenDTO.builder()
                .accessToken(token.getAccessToken())
                .refreshToken(token.getRefreshToken())
                .tokenType(TokenTypes.BEARER)
                .expiresIn(ACCESS_TOKEN_EXPIRY_MINUTES * 60)
                .build();
    }
}
