package com.v01.techgear_server.controller.Authentication;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import org.apache.commons.lang3.StringUtils;
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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.v01.techgear_server.dto.ApiResponseDTO;
import com.v01.techgear_server.dto.TokenDTO;
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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v01/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication API", description = "API for authentication controller API")
public class AuthController {

    @Qualifier("jwtRefreshTokenAuthProvider")
    JwtAuthenticationProvider refreshTokenAuthProvider;

    private final UserDetailsManager userDetailsManager;
    private final UserRepository userRepository;
    private final TokenGenerator tokenGenerator;
    private final EmailService emailService;
    private final AuthServiceImpl userDetailsServiceImpl;
    private final UserService userService;
    private final DaoAuthenticationProvider daoAuthenticationProvider;
    private final TokenService customTokenService;

    private final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);

    @PostMapping("/register")
    @Operation(summary = "User  registered a new account", description = "Returns a successful response with the user data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User  registered successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDTO.class))),
            @ApiResponse(responseCode = "409", description = "Conflict occurred during registration", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Bad request due to invalid input", content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<ApiResponseDTO<User>> userRegistration(
            @Parameter(description = "User  details in JSON Format", required = true) @RequestPart("user") String userJson,
            @Parameter(description = "User  avatar file", required = true) @RequestPart(value = "userAvatar") MultipartFile userAvatar)
            throws  JsonProcessingException {

        // Deserialize the JSON string to a User object
        User user;
        try {
            user = new ObjectMapper().readValue(userJson, User.class);
        } catch (JsonProcessingException e) {
            LOGGER.error("Failed to parse user JSON: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponseBuilder.createErrorResponse(ApiResponseStatus.INVALID_INPUT));
        }

        // Use CompletableFuture to handle tasks asynchronously
        CompletableFuture<Void> userCreationFuture = CompletableFuture.runAsync(() -> {
            userDetailsManager.createUser(user);
            userService.userUploadAvatar(user, userAvatar);
            emailService.sendVerificationEmail(user);
        });

        // Handle potential exceptions
        try {
            userCreationFuture.join(); // Wait for the completion of the async task
        } catch (CompletionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof IllegalArgumentException) {
                LOGGER.error("Registration failed: {}", cause.getMessage());
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(ApiResponseBuilder.createErrorResponse(ApiResponseStatus.REGISTRATION_FAILED));
            } else {
                LOGGER.error("Unexpected error during registration: {}", cause.getMessage(), cause);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(ApiResponseBuilder.createErrorResponse(ApiResponseStatus.INTERNAL_SERVER_ERROR));
            }
        }

        // Log the user registration
        LOGGER.info("User  registered successfully: {}", user);

        // Return a successful response with the user data
        return ResponseEntity
                .ok(ApiResponseBuilder.createSuccessResponse(user, ApiResponseStatus.REGISTRATION_SUCCESS));
    }

    @GetMapping("/verify-email")
    @Operation(summary = "Verify user email", description = "Verifies user email using provided token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Email verified successfully"),
            @ApiResponse(responseCode = "400", description = "Email verification failed")
    })
    public ResponseEntity<ApiResponseDTO<String>> verifyEmail(
            @Parameter(description = "Email verification request", required = true) @Valid @RequestParam("token") String token) {

        // Validate request body
        if (StringUtils.isBlank(token)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponseBuilder.createErrorResponse(
                            ApiResponseStatus.INVALID_TOKEN,
                            "Verification token cannot be empty"));
        }

        try {
            // Perform email verification
            String verificationResult = emailService.verifyEmail(token);

            // Log successful verification with masked token
            LOGGER.info("Email verification successful for token: {}", maskToken(token));

            // Return successful response
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponseBuilder.createSuccessResponse(
                            verificationResult,
                            ApiResponseStatus.VERIFY_EMAIL_SUCCESS));

        } catch (IllegalStateException e) {
            // Handle specific token-related exceptions
            LOGGER.warn("Email verification failed: {}", e.getMessage());
            return createErrorResponse(
                    HttpStatus.BAD_REQUEST,
                    ApiResponseStatus.INVALID_TOKEN,
                    e.getMessage());

        } catch (AuthenticationException e) {
            // Handle authentication-related exceptions
            LOGGER.error("Authentication error during email verification", e);
            return createErrorResponse(
                    HttpStatus.UNAUTHORIZED,
                    ApiResponseStatus.UNAUTHORIZED,
                    "Authentication failed during email verification");

        } catch (Exception e) {
            // Catch any unexpected errors
            LOGGER.error("Unexpected error during email verification", e);
            return createErrorResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ApiResponseStatus.INTERNAL_SERVER_ERROR,
                    "An unexpected error occurred");
        }
    }

    /**
     * Create a standardized error response
     * 
     * @param status         HTTP status
     * @param responseStatus API response status
     * @param message        Error message
     * @return ResponseEntity with error response
     */
    private ResponseEntity<ApiResponseDTO<String>> createErrorResponse(
            HttpStatus status,
            ApiResponseStatus responseStatus,
            String message) {

        return ResponseEntity.status(status)
                .body(ApiResponseBuilder.createErrorResponse(
                        responseStatus,
                        message));
    }

    /**
     * Masks the token for logging purposes to prevent sensitive information
     * exposure
     * 
     * @param token Original token
     * @return Masked token
     */
    private String maskToken(String token) {
        if (StringUtils.isBlank(token)) {
            return "";
        }

        return Optional.ofNullable(token)
                .filter(t -> t.length() > 8)
                .map(t -> {
                    int length = t.length();
                    return t.substring(0, 4) +
                            StringUtils.repeat("*", length - 8) +
                            t.substring(length - 4);
                })
                .orElse(StringUtils.repeat("*", token.length()));
    }

    @PostMapping("/resend-verification-email")
    @Operation(summary = "resend email verification token", description = "Returns a successful response with the user data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email verification token resent successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Bad request due to invalid input", content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<String> resendVerificationEmail(
            @Parameter(description = "User email", required = true) @RequestParam("email") String email) {
        try {
            emailService.resendVerificationEmail(email);
            return ResponseEntity.ok("Verification email sent successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error sending verification email" + e.getMessage());
        }
    }

    // BEGIN USER LOGIN
    @PostMapping("/login")
    @Operation(summary = "User login", description = "Returns a successful response with the user data and JWT tokens")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User logged in successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Bad request due to invalid input", content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<ApiResponseDTO<Map<String, Object>>> login(
            @Parameter(description = "User details in JSON Format", required = true) @RequestBody User user,
            HttpServletResponse response) {

        if (isUserInputInvalid(user)) {
            return buildBadRequestResponse();
        }

        try {
            Authentication authentication = authenticateUser(user);
            User authenticatedUser = retrieveAuthenticatedUser(authentication);

            if (!isUserActive(authenticatedUser)) {
                return buildForbiddenResponse();
            }

            TokenDTO token = generateAndStoreToken(authentication, response, authenticatedUser);
            Map<String, Object> responseData = buildResponseData(authenticatedUser, token);

            return ResponseEntity
                    .ok(ApiResponseBuilder.createSuccessResponse(responseData, ApiResponseStatus.LOGIN_SUCCESS));

        } catch (AuthenticationException e) {
            return buildUnauthorizedResponse();
        }
    }

    private boolean isUserInputInvalid(User user) {
        return user.getUsername() == null || user.getPassword() == null;
    }

    private ResponseEntity<ApiResponseDTO<Map<String, Object>>> buildBadRequestResponse() {
        return ResponseEntity.badRequest()
                .body(ApiResponseBuilder.createErrorResponse(ApiResponseStatus.BAD_REQUEST));
    }

    private Authentication authenticateUser(User user) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                user.getUsername(), user.getPassword());
        return daoAuthenticationProvider.authenticate(authenticationToken);
    }

    private User retrieveAuthenticatedUser(Authentication authentication) {
        return (User) authentication.getPrincipal();
    }

    private boolean isUserActive(User user) {
        return user.isActive();
    }

    private ResponseEntity<ApiResponseDTO<Map<String, Object>>> buildForbiddenResponse() {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponseBuilder.createErrorResponse(ApiResponseStatus.EMAIL_NOT_VERIFIED));
    }

    private TokenDTO generateAndStoreToken(Authentication authentication, HttpServletResponse response, User user) {
        TokenDTO token = tokenGenerator.generateTokens(authentication);
        setRefreshTokenCookie(response, token);
        
        return token;
    }

    private void setRefreshTokenCookie(HttpServletResponse response, TokenDTO token) {
        Cookie cookie = new Cookie("refreshToken", token.getRefreshToken());
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(7 * 24 * 60 * 60); // 7 days
        response.addCookie(cookie);
    }

    private Map<String, Object> buildResponseData(User user, TokenDTO token) {
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("user", user);
        responseData.put("token", token);
        return responseData;
    }

    private ResponseEntity<ApiResponseDTO<Map<String, Object>>> buildUnauthorizedResponse() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponseBuilder.createErrorResponse(ApiResponseStatus.INVALID_CREDENTIALS));
    }

    // END USER LOGIN

    @PostMapping("/update-password")
    @Operation(summary = "update password", description = "Returns a successful response with the user data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Bad request due to invalid input", content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<ApiResponseDTO<String>> updatePassword(
            @Parameter(description = "Old password", required = true) @RequestParam("oldPassword") String oldPassword,
            @Parameter(description = "New password", required = true) @RequestParam("newPassword") String newPassword) {
        try {
            userDetailsManager.changePassword(oldPassword, newPassword);

            return ResponseEntity.status(HttpStatus.OK).body(
                    ApiResponseBuilder.createSuccessResponse(newPassword, ApiResponseStatus.UPDATE_PASSWORD_SUCCESS));

        } catch (Exception e) {
            LOGGER.error("Failed to update password", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseBuilder.createErrorResponse(ApiResponseStatus.ERROR_UPDATING_PASSWORD));
        }
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "forgot password", description = "Returns a successful response with the user data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password reset token sent to the email", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<String> forgotPassword(
            @Parameter(description = "Email of the user", required = true) @RequestParam("email") String email) {
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
    @Operation(summary = "reset password", description = "Returns a successful response with the user data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password reset successfully", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Bad request due to invalid input", content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<ApiResponseDTO<String>> resetPassword(
            @Parameter(description = "Password reset token", required = true) @RequestParam("token") String token,
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
    public ResponseEntity<ApiResponseDTO<TokenDTO>> refreshToken(@RequestBody Token tokenRequest) {
        try {
            // Validate the token first
            if (!customTokenService.isTokenValid(tokenRequest.getRefreshToken())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponseBuilder.createErrorResponse(ApiResponseStatus.INVALID_REFRESH_TOKEN));
            }

            // Authenticate the refresh token
            final Authentication authentication = refreshTokenAuthProvider.authenticate(
                    new BearerTokenAuthenticationToken(tokenRequest.getRefreshToken()));

            // Extract JWT from the authentication object
            final Jwt jwt = (Jwt) authentication.getCredentials();

            // Check token expiration
            if (isTokenExpired(jwt)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponseBuilder.createErrorResponse(ApiResponseStatus.ACCESS_TOKEN_HAS_EXPIRED));
            }

            // Load user ID and handle potential multiple results
            final Long userId = Long.parseLong(jwt.getSubject());
            final Optional<User> userOpt = userRepository.findById(userId);

            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponseBuilder.createErrorResponse(ApiResponseStatus.USER_NOT_FOUND));
            }

            final User user = userOpt.get();

            // Create new authentication
            final Authentication newAuthentication = new UsernamePasswordAuthenticationToken(user,
                    null,
                    user.getAuthorities());

            // Generate new tokens
            final TokenDTO newTokens = tokenGenerator.generateTokens(newAuthentication);

            // Revoke old tokens
            customTokenService.revokeAllUserTokens(userId);

            return ResponseEntity.ok(ApiResponseBuilder.createSuccessResponse(
                    newTokens, ApiResponseStatus.GENERATE_TOKEN_SUCCESSFUL));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseBuilder.createErrorResponse(ApiResponseStatus.INTERNAL_SERVER_ERROR));
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
