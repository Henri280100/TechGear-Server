package com.v01.techgear_server.controller.Authentication;

import java.time.Instant;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.v01.techgear_server.dto.UserDTO;
import com.v01.techgear_server.model.Login;
import com.v01.techgear_server.model.SignUp;
import com.v01.techgear_server.model.Token;
import com.v01.techgear_server.model.User;
import com.v01.techgear_server.security.TokenGenerator;
import com.v01.techgear_server.serviceImpls.AuthServiceImpl;
import com.v01.techgear_server.serviceImpls.TokenServiceImpl;

@RestController
@RequestMapping("/api/v01/auth")
public class AuthController {
    @Autowired
    private AuthServiceImpl authService;

    @Autowired
    private TokenGenerator tokenGenerator;

    @Autowired
    private DaoAuthenticationProvider daoAuthenticationProvider;

    @Autowired
    @Qualifier("jwtRefreshTokenAuthProvider")
    private JwtAuthenticationProvider refreshTokenAuthProvider;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private TokenServiceImpl customTokenService;

    @PostMapping("/register")
    public ResponseEntity<?> userRegistration(@RequestBody SignUp signupDTO) {
        try {

            UserDTO userDTO = modelMapper.map(signupDTO, UserDTO.class);
            authService.createUser((UserDetails) userDTO);

            return ResponseEntity.ok(tokenGenerator.createToken(
                    new UsernamePasswordAuthenticationToken(signupDTO.getUserName(), signupDTO.getPassword(), null)));
        } catch (Exception e) {
            e.getMessage();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error while registering user");
        }

    }

    @PostMapping("/login")
    public ResponseEntity<?> userLogin(@RequestBody Login loginDTO) {
        Authentication authentication = daoAuthenticationProvider.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(loginDTO.getUserName(), loginDTO.getPassword()));

        return ResponseEntity.ok(tokenGenerator.createToken(authentication));
    }

    @PostMapping("/token")
    public ResponseEntity<?> token(@RequestBody Token tokenDTO) {
        try {
            // Authenticate the refresh token
            Authentication authentication = refreshTokenAuthProvider.authenticate(
                    new BearerTokenAuthenticationToken(tokenDTO.getRefreshToken()));

            // Extract JWT from the authentication object
            Jwt jwt = (Jwt) authentication.getCredentials();

            // Validate the token using the custom token service
            if (!customTokenService.isTokenValid(tokenDTO.getRefreshToken())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid or revoked refresh token");
            }

            // Check token expiration manually if needed
            if (isTokenExpired(jwt)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Refresh token has expired");
            }

            return ResponseEntity.ok(tokenGenerator.createToken(authentication));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while processing the token");
        }

    }

    private boolean isTokenExpired(Jwt jwt) {
        if (jwt == null || jwt.getExpiresAt() == null) {
            return true; // If there's no expiration time, consider it expired.
        }

        Instant expiration = jwt.getExpiresAt(); // Extract expiration time from the JWT
        return Instant.now().isAfter(expiration); // Check if the current time is after the expiration time
    }
}
