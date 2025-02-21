package com.v01.techgear_server.security;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import com.v01.techgear_server.dto.TokenDTO;
import com.v01.techgear_server.exception.InvalidTokenException;
import com.v01.techgear_server.mapping.TokenMapper;
import com.v01.techgear_server.model.Token;
import com.v01.techgear_server.model.User;
import com.v01.techgear_server.repo.jpa.TokenRepository;
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
			log.info("Generated new tokens for user: {}. Expires at: {}", token.getUserId(), token.getExpiresDate());
			return buildTokenDTO(token);
		} catch (Exception e) {
			log.error("Error generating tokens: ", e);
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

	private String createRefreshToken(User user, Instant now) {

		JwtClaimsSet claims = JwtClaimsSet.builder()
		                                  .issuer(ISSUER)
		                                  .issuedAt(now)
		                                  .expiresAt(now.plus(REFRESH_TOKEN_EXPIRY_DAYS, ChronoUnit.DAYS))
		                                  .subject(user.getUserId()
		                                               .toString())
		                                  .claim("type", "refresh_token")
		                                  .build();
		validateAndRevokeOldToken(user.getUserId()
		                              .toString());
		return refreshTokenEncoder.encode(JwtEncoderParameters.from(claims))
		                          .getTokenValue();
	}

	private void validateAndRevokeOldToken(String refreshToken) {
		Token oldToken = tokenRepository.findLatestValidRefreshToken(refreshToken)
		                                .orElseThrow(() -> {
			                                log.warn("Refresh token not found or revoked: {}", refreshToken);
			                                return new InvalidTokenException("Refresh token not found or revoked");
		                                });
		oldToken.setRevoked(true);
		tokenRepository.save(oldToken);
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
