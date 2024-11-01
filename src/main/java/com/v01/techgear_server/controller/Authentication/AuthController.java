package com.v01.techgear_server.controller.Authentication;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
import org.springframework.security.core.context.SecurityContextHolder;
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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.v01.techgear_server.dto.ApiResponse;
import com.v01.techgear_server.enums.ApiResponseStatus;
import com.v01.techgear_server.model.PasswordResetToken;
import com.v01.techgear_server.model.Token;
import com.v01.techgear_server.model.User;
import com.v01.techgear_server.repo.UserRepository;
import com.v01.techgear_server.security.TokenGenerator;
import com.v01.techgear_server.service.EmailService;
import com.v01.techgear_server.service.TokenService;
import com.v01.techgear_server.service.UserService;
import com.v01.techgear_server.serviceImpls.AuthServiceImpl;
import com.v01.techgear_server.utils.ApiResponseBuilder;

import jakarta.servlet.http.Cookie;
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

    private final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<User>> userRegistration(@RequestPart("user") String userJson,
            @RequestPart(value = "userAvatar") MultipartFile userAvatar)
            throws JsonMappingException, JsonProcessingException {
        try {
            // Deserialize the JSON string to a User object
            User user = new ObjectMapper().readValue(userJson, User.class);

            // Create the user
            userDetailsManager.createUser(user);

            // Handle user avatar upload
            userService.userUploadAvatarHandler(user, userAvatar);

            // Send verification email
            emailService.sendVerificationEmail(user);

            // Log the user registration
            LOGGER.info("User  registered successfully: {}", user);

            // Return a successful response with the user data
            return ResponseEntity
                    .ok(ApiResponseBuilder.createSuccessResponse(user, ApiResponseStatus.REGISTRATION_SUCCESS));

        } catch (IllegalArgumentException e) {
            LOGGER.error("Registration failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ApiResponseBuilder.createErrorResponse(ApiResponseStatus.REGISTRATION_FAILED));
        } catch (Exception e) {
            LOGGER.error("Failed to authenticate: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponseBuilder.createErrorResponse(ApiResponseStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @GetMapping("/verify-email")
    public ResponseEntity<ApiResponse<String>> verifyEmail(@RequestParam("token") String token) {
        try {
            String result = emailService.verifyEmail(token);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponseBuilder.createSuccessResponse(result, ApiResponseStatus.VERIFY_EMAIL_SUCCESS));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponseBuilder.createErrorResponse(ApiResponseStatus.ERROR_VERIFIED_EMAIL));
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
    public ResponseEntity<ApiResponse<Map<String, Object>>> userLogin(@RequestBody User user,
            HttpServletResponse response) {
        if (user.getUsername() == null || user.getPassword() == null) {
            return ResponseEntity.badRequest()
                    .body(ApiResponseBuilder.createErrorResponse(ApiResponseStatus.BAD_REQUEST));
        }

        // Create authentication token
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                user.getUsername(), user.getPassword());

        try {
            // Authenticate
            Authentication authentication = daoAuthenticationProvider.authenticate(authenticationToken);

            if (authentication.getPrincipal() instanceof User) {
                user = (User) authentication.getPrincipal();
            } else {
                // If it's not a User instance, load it from the database
                user = (User) userDetailsManager.loadUserByUsername(authentication.getName());
            }

            // Check if the email is verified
            if (!user.isActive()) { // Assuming there is a method to check email verification
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponseBuilder.createErrorResponse(ApiResponseStatus.EMAIL_NOT_VERIFIED));
            }

            // Generate token
            Token token = tokenGenerator.createToken(authentication);
            // Set refresh token in HttpOnly cookie
            Cookie cookie = new Cookie("refreshToken", token.getRefreshToken());
            cookie.setHttpOnly(true);
            cookie.setSecure(true);
            cookie.setPath("/");
            cookie.setMaxAge(7 * 24 * 60 * 60);
            response.addCookie(cookie);
            // Create a response object combining user and token info
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("user", user);
            responseData.put("token", token);

            return ResponseEntity
                    .ok(ApiResponseBuilder.createSuccessResponse(responseData, ApiResponseStatus.LOGIN_SUCCESS));

        } catch (AuthenticationException e) {
            LOGGER.error("Authentication failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponseBuilder.createErrorResponse(ApiResponseStatus.INVALID_CREDENTIALS));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            try {
                String userIdString = auth.getName();
                Long userId = Long.parseLong(userIdString);
                customTokenService.revokeAllUserTokens(userId);

                new SecurityContextLogoutHandler().logout(request, response, auth);
                request.getSession().invalidate();
                SecurityContextHolder.clearContext();
                return ResponseEntity
                        .ok(ApiResponseBuilder.createSuccessResponse(userIdString, ApiResponseStatus.LOGOUT_SUCCESS));
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(ApiResponseBuilder.createErrorResponse(ApiResponseStatus.INTERNAL_SERVER_ERROR));
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponseBuilder.createErrorResponse(ApiResponseStatus.UNAUTHORIZED));
    }

    @PostMapping("/update-password")
    public ResponseEntity<ApiResponse<String>> updatePassword(@RequestParam("oldPassword") String oldPassword,
            @RequestParam("newPassword") String newPassword) {
        try {
            userDetailsManager.changePassword(oldPassword, newPassword);

            return ResponseEntity.status(HttpStatus.OK).body(
                    ApiResponseBuilder.createSuccessResponse(newPassword, ApiResponseStatus.UPDATE_PASSWORD_SUCCESS));

        } catch (Exception e) {
            LOGGER.error("Failed to update password", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseBuilder.createErrorResponse(ApiResponseStatus.ERROR_UPDATING_PASSWORD));
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam("email") String email) {
        // Find the user by email
        User user = userDetailsServiceImpl.findUserByEmail(email);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponseStatus.INVALID_EMAIL);
        }

        // Generate the password reset token
        String token = UUID.randomUUID().toString();

        // Call the service to create and save the token
        userDetailsServiceImpl.onPasswordResetToken(user, token);

        // Send email with the reset token (link to reset password)
        emailService.sendResetTokenEmail(user.getEmail(), token);

        return ResponseEntity.ok("Password reset token sent to your email.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<String>> resetPassword(@RequestParam("token") String token,
            @RequestParam("newPassword") String newPassword) {
        // Verify if the token is valid and not expired
        PasswordResetToken passwordResetToken = userDetailsServiceImpl.validatePasswordResetToken(token);
        if (passwordResetToken == null || passwordResetToken.isExpired()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponseBuilder.createErrorResponse(ApiResponseStatus.INVALID_CREDENTIALS));
        }

        // Reset the user's password
        userDetailsServiceImpl.updateUserPassword(passwordResetToken.getUser(), newPassword);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponseBuilder.createSuccessResponse(newPassword, ApiResponseStatus.RESET_PASSWORD_SUCCESS));
    }

    @PostMapping("/token")
    public ResponseEntity<?> token(@RequestBody Token tokenDTO) {
        try {
            // Validate the refresh token expiration
            if (isRefreshTokenExpired(tokenDTO.getRefreshToken())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Refresh token has expired");
            }

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
                        .body("Access token has expired");
            }

            return ResponseEntity.ok(tokenGenerator.createToken(authentication));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while processing the token");
        }
    }

    private boolean isRefreshTokenExpired(String refreshToken) {
        // Retrieve the expiration time of the refresh token from your token service or
        // database
        Instant expirationTime = customTokenService.getRefreshTokenExpiration(refreshToken);

        // If expirationTime is null, consider it expired
        if (expirationTime == null) {
            return true;
        }

        // Check if the current time is after the expiration time
        return Instant.now().isAfter(expirationTime);
    }

    private boolean isTokenExpired(Jwt jwt) {
        if (jwt == null || jwt.getExpiresAt() == null) {
            return true; // If there's no expiration time, consider it expired.
        }

        Instant expiration = jwt.getExpiresAt(); // Extract expiration time from the JWT
        return Instant.now().isAfter(expiration); // Check if the current time is after the expiration time
    }
}
