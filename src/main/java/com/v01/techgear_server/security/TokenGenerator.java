package com.v01.techgear_server.security;

import java.text.MessageFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import com.v01.techgear_server.model.Token;
import com.v01.techgear_server.model.User;

@Component
public class TokenGenerator {
    @Autowired
    JwtEncoder accessTokenEncoder;

    @Autowired
    @Qualifier("jwtRefreshTokenEncoder")
    JwtEncoder refreshTokenEncoder;

    /**
     * Creates a JWT access token for the given authentication object.
     *
     * The access token is issued by "techgear_server" and expires in 5 minutes.
     * The subject of the token is the user ID obtained from the authentication
     * principal.
     *
     * @param authentication the authentication object containing the principal and
     *                       credentials
     * @return a JWT access token as a string
     * @throws ClassCastException if the principal is not of type User
     */
    public String createAccessToken(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Instant now = Instant.now();

        JwtClaimsSet claimsSet = JwtClaimsSet.builder()
                .issuer("techgear_server").issuedAt(now).expiresAt(now.plus(5, ChronoUnit.MINUTES))
                .subject(user.getUser_id().toString()).claim("authorities", authentication.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority).toList())
                .build();

        return accessTokenEncoder.encode(JwtEncoderParameters.from(claimsSet)).getTokenValue();
    }

    public String createRefreshToken(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Instant now = Instant.now();

        JwtClaimsSet claimsSet = JwtClaimsSet.builder()
                .issuer("techgear_server").issuedAt(now).expiresAt(now.plus(7, ChronoUnit.DAYS))
                .subject(user.getUser_id().toString()).build();

        return refreshTokenEncoder.encode(JwtEncoderParameters.from(claimsSet)).getTokenValue();
    }

    /**
     * Creates a new token for the given authentication object.
     *
     * If the refresh token is close to expiring (less than 7 days), a new refresh
     * token is generated.
     *
     * @param authentication the authentication object containing the principal and
     *                       credentials
     * @return a new TokenDTO containing the access token and refresh token
     * @throws BadCredentialsException  if the principal is not of type User
     * @throws IllegalArgumentException if the user ID is null
     */
    public Token createToken(Authentication authentication) {
        if (!(authentication.getPrincipal() instanceof User user)) {
            throw new BadCredentialsException(
                    MessageFormat.format("principal {0} is not of User type",
                            authentication.getPrincipal().getClass()));
        }
        Token userToken = new Token();

        if (user.getUser_id() == null) {
            throw new IllegalArgumentException("User ID is null. The user might not have been saved properly.");
        }

        userToken.setUser_id(user.getUser_id());
        userToken.setAccessToken(createAccessToken(authentication));

        String refreshToken;
        if (authentication.getCredentials() instanceof Jwt jwt) {
            Instant now = Instant.now();
            Instant expiresAt = jwt.getExpiresAt();
            Duration duration = Duration.between(now, expiresAt);

            long daysUntilExpired = duration.toDays();
            if (daysUntilExpired < 7) {
                refreshToken = createRefreshToken(authentication);
            } else {
                refreshToken = jwt.getTokenValue();
            }
        } else {
            refreshToken = createRefreshToken(authentication);
        }
        userToken.setRefreshToken(refreshToken);

        return userToken;
    }

}
