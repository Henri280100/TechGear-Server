package com.v01.techgear_server.controller.Authentication;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.v01.techgear_server.dto.UserDTO;
import com.v01.techgear_server.model.PasswordResetToken;
import com.v01.techgear_server.model.Token;
import com.v01.techgear_server.model.User;
import com.v01.techgear_server.repo.UserRepository;
import com.v01.techgear_server.security.TokenGenerator;
import com.v01.techgear_server.service.EmailService;

import com.v01.techgear_server.service.TokenService;
import com.v01.techgear_server.service.UserService;
import com.v01.techgear_server.serviceImpls.AuthServiceImpl;
import com.v01.techgear_server.utils.PasswordValidation;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/v01/auth")
public class AuthController {
    @Autowired
    UserDetailsManager userDetailsManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    TokenGenerator tokenGenerator;

    @Autowired
    private EmailService emailService;

    @Autowired
    AuthServiceImpl userDetailsServiceImpl;

    @Autowired
    private UserService userService;

    @Autowired
    DaoAuthenticationProvider daoAuthenticationProvider;

    @Autowired
    @Qualifier("jwtRefreshTokenAuthProvider")
    JwtAuthenticationProvider refreshTokenAuthProvider;

    @Autowired
    private TokenService customTokenService;

    @Autowired
    ModelMapper modelMapper;

    private final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);

    @Async
    @PostMapping("/register")
    public ResponseEntity<?> userRegistration(@RequestPart("userDTO") String userDTOJson,
            @RequestPart(value = "userAvatar", required = false) MultipartFile userAvatar) {
        try {
            User user = new ObjectMapper().readValue(userDTOJson, User.class);

            user.setPassword(PasswordValidation.encodePassword(user.getPassword()));

            userRepository.save(user);

            if (userAvatar != null && !userAvatar.isEmpty()) {

                userService.userUploadAvatarHandler(user.getId(), userAvatar);
            }

            userDetailsManager.createUser(user);

            emailService.sendVerificationEmail(user);

            return ResponseEntity.status(HttpStatus.CREATED).body("Registered successfully");

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

            UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(user.getUsername());

            if (!PasswordValidation.matchesPassword(userDto.getPassword(), userDetails.getPassword())) {
                LOGGER.warn("Encountered invalid password or encrypted password does not match");
            }

            Authentication authentication = daoAuthenticationProvider.authenticate(
                    UsernamePasswordAuthenticationToken.unauthenticated(user.getUsername(), user.getPassword()));

            tokenGenerator.createToken(authentication);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body("Login successfully authenticated");
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return ResponseEntity.ok("Successfully logged out");
    }

    @PostMapping("/update-password")
    public ResponseEntity<String> updatePassword(@RequestParam("oldPassword") String oldPassword,
            @RequestParam("newPassword") String newPassword) {
        try {
            userDetailsManager.changePassword(oldPassword, newPassword);

            return ResponseEntity.status(HttpStatus.OK).body("Update user password successfully");

        } catch (Exception e) {
            LOGGER.error("Failed to update password", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error while updating password");
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam("email") String email) {
        // Find the user by email
        UserDTO userDTO = userDetailsServiceImpl.findUserByEmail(email);
        if (userDTO == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Email address not found");
        }

        // Generate the password reset token
        String token = UUID.randomUUID().toString();

        // Call the service to create and save the token
        userDetailsServiceImpl.onPasswordResetToken(userDTO, token);

        // Send email with the reset token (link to reset password)
        emailService.sendResetTokenEmail(userDTO.getEmail(), token);

        return ResponseEntity.ok("Password reset token sent to your email.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam("token") String token,
            @RequestParam("newPassword") String newPassword) {
        // Verify if the token is valid and not expired
        PasswordResetToken passwordResetToken = userDetailsServiceImpl.validatePasswordResetToken(token);
        if (passwordResetToken == null || passwordResetToken.isExpired()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired token.");
        }

        // Reset the user's password
        userDetailsServiceImpl.updateUserPassword(passwordResetToken.getUser(), newPassword);

        return ResponseEntity.ok("Password has been successfully reset.");
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
