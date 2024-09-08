package com.v01.techgear_server.controller.Authentication;

import java.time.Instant;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.v01.techgear_server.dto.UserDTO;
import com.v01.techgear_server.model.Token;
import com.v01.techgear_server.model.User;
import com.v01.techgear_server.repo.UserRepository;
import com.v01.techgear_server.security.TokenGenerator;
import com.v01.techgear_server.serviceImpls.EmailServiceImpl;
import com.v01.techgear_server.serviceImpls.TokenServiceImpl;

@RestController
@RequestMapping("/api/v01/auth")
public class AuthController {
    @Autowired
    private UserDetailsManager userDetailsManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenGenerator tokenGenerator;

    @Autowired
    private EmailServiceImpl emailService;

    @Autowired
    private DaoAuthenticationProvider daoAuthenticationProvider;

    @Autowired
    @Qualifier("jwtRefreshTokenAuthProvider")
    private JwtAuthenticationProvider refreshTokenAuthProvider;

    @Autowired
    private TokenServiceImpl customTokenService;

    @Autowired
    private ModelMapper modelMapper;

    private final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);

    @PostMapping("/register")
    public ResponseEntity<?> userRegistration(@RequestBody UserDTO userDTO) {
        try {

            User user = modelMapper.map(userDTO, User.class);
            user.setActive(false);
            userRepository.save(user);
            userDetailsManager.createUser(user);

            LOGGER.info("Successfully register: {}", user);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("User registered successfully, please verify your email" + user);

        } catch (Exception e) {

            LOGGER.error("Failed to authenticate", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error while registering user");
        }

    }

    @GetMapping("/verify-email")
    public ResponseEntity<String> verifyEmail(@RequestParam("token") String token) {
        try {
            String result = emailService.verifyEmail(token);
            return ResponseEntity.ok(result);
        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error verifying email" + e.getMessage());
        }
    }

    @PostMapping("/resend-verification-email")
    public ResponseEntity<String> resendVerificationEmail(@RequestParam("email") String email) {
        try {
            emailService.resendVerificationEmail(email);
            return ResponseEntity.ok("Verification email sent successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error sending verification email" + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> userLogin(@RequestBody UserDTO userDto) {
        try {
            User user = modelMapper.map(userDto, User.class);

            Authentication authentication = daoAuthenticationProvider.authenticate(
                    UsernamePasswordAuthenticationToken.unauthenticated(user.getUsername(), user.getPassword()));

            user = (User) authentication.getPrincipal();
            if (!user.isActive()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Email not verified. Please verify your email.");
            }

            return ResponseEntity.ok(tokenGenerator.createToken(authentication));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
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
