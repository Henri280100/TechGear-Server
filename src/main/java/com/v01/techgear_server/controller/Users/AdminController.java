package com.v01.techgear_server.controller.Users;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.v01.techgear_server.dto.ApiResponseDTO;
import com.v01.techgear_server.dto.UserSearchCriteria;
import com.v01.techgear_server.enums.ApiResponseStatus;
import com.v01.techgear_server.enums.Roles;
import com.v01.techgear_server.enums.UserStatus;
import com.v01.techgear_server.exception.UserNotFoundException;
import com.v01.techgear_server.model.User;
import com.v01.techgear_server.repo.UserRepository;
import com.v01.techgear_server.service.UserService;
import com.v01.techgear_server.utils.ApiResponseBuilder;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v01/admin")
@RequiredArgsConstructor
public class AdminController {
        // Define allowed sort fields
        private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
                        "userId", "username", "email", "createdAt", "status");
        private final UserService userService;
        // un-comment when you need to use it
        // private final UserRepository userRepository;

        @GetMapping("/{userId}")
        @PreAuthorize("hasRole('ADMIN') and hasPermission(#userId, 'USER', 'USER_READ')")
        @Operation(summary = "Get a user by ID", description = "Returns the user with the specified ID")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "User found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDTO.class))),
                        @ApiResponse(responseCode = "404", description = "User not found"),
        })
        public CompletableFuture<ResponseEntity<ApiResponseDTO<User>>> getUserEntity(
                        @Parameter(description = "ID of the user to retrieve", required = true) @PathVariable Long userId) {
                // Validate input
                if (userId == null) {
                        return CompletableFuture.completedFuture(
                                        ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                                        .body(ApiResponseBuilder.createErrorResponse(
                                                                        ApiResponseStatus.USER_ID_NULL)));
                }

                return CompletableFuture.supplyAsync(() -> {
                        // Pre-processing or additional validation can be done here
                        validateUserId(userId);

                        return userService.getUserById(userId);
                })
                                .thenCompose(Function.identity()) // Flatten the CompletableFuture
                                .thenApply(user -> ResponseEntity.status(HttpStatus.OK)
                                                .body(ApiResponseBuilder.createSuccessResponse(user,
                                                                ApiResponseStatus.RETRIEVE_USER_SUCCESS)))
                                .exceptionally(ex -> {
                                        if (ex.getCause() instanceof UserNotFoundException) {
                                                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                                                .body(ApiResponseBuilder.createErrorResponse(
                                                                                ApiResponseStatus.ERROR_RETRIEVE_USER));
                                        }

                                        if (ex.getCause() instanceof ValidationException) {
                                                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                                                .body(ApiResponseBuilder.createErrorResponse(
                                                                                ApiResponseStatus.INVALID_USER_ID));
                                        }

                                        // Log unexpected errors
                                        log.error("Unexpected error retrieving user", ex);

                                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                                        .body(ApiResponseBuilder.createErrorResponse(
                                                                        ApiResponseStatus.UNEXPECTED_ERROR));
                                });
        }

        @GetMapping("/find-by-username-or-email")
        @PreAuthorize("hasRole('ADMIN')")
        @Operation(summary = "Find user by username or email", description = "Returns the user with the specified username or email")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "User found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDTO.class))),
                        @ApiResponse(responseCode = "404", description = "User not found"),
        })
        public CompletableFuture<ResponseEntity<ApiResponseDTO<CompletableFuture<User>>>> getUserByUsernameOrEmail(
                        @Parameter(description = "Username or email of the user to retrieve", required = true) @RequestParam String username,
                        @RequestParam String email) {
                // Validate input
                if (StringUtils.isEmpty(username) || StringUtils.isEmpty(email)) {
                        return CompletableFuture.completedFuture(
                                        ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                                        .body(ApiResponseBuilder.createErrorResponse(
                                                                        ApiResponseStatus.USERNAME_EMAIL_NOT_FOUND)));
                }

                return CompletableFuture.supplyAsync(() -> userService.findUserByUsernameOrEmail(username, email))
                                .thenApply(user -> ResponseEntity.status(HttpStatus.OK)
                                                .body(ApiResponseBuilder.createSuccessResponse(user,
                                                                ApiResponseStatus.RETRIEVE_USER_SUCCESS)))
                                .exceptionally(ex -> {
                                        if (ex.getCause() instanceof UserNotFoundException) {
                                                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                                                .body(ApiResponseBuilder.createErrorResponse(
                                                                                ApiResponseStatus.ERROR_RETRIEVE_USER));
                                        }

                                        // Log unexpected errors
                                        log.error("Unexpected error retrieving user", ex);

                                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                                        .body(ApiResponseBuilder.createErrorResponse(
                                                                        ApiResponseStatus.UNEXPECTED_ERROR));
                                });
        }

        @GetMapping("/all-users")
        @PreAuthorize("hasRole('ADMIN')")
        @Operation(summary = "Get all users", description = "Returns a paginated list of users")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Get all all users successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDTO.class))),
                        @ApiResponse(responseCode = "403", description = "Access denied"),
        })
        public CompletableFuture<ResponseEntity<ApiResponseDTO<CompletableFuture<Page<User>>>>> getAllUsers(
                        @Parameter(description = "Sort by field") @RequestParam(defaultValue = "userId") String sortBy,

                        @Parameter(description = "Sort order") @RequestParam(defaultValue = "asc") String direction,

                        @Parameter(description = "Page number") @RequestParam(defaultValue = "1") Integer pageNo,

                        @Parameter(description = "Page size") @RequestParam(defaultValue = "10") Integer pageSize,

                        @Parameter(description = "User status filter") @RequestParam(required = false) UserStatus status,

                        @Parameter(description = "Minimum creation date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @RequestParam(required = false) LocalDateTime minCreatedAt,

                        @Parameter(description = "Maximum creation date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @RequestParam(required = false) LocalDateTime maxCreatedAt) {

                return CompletableFuture.supplyAsync(() -> {
                        // Comprehensive input validation and criteria building
                        UserSearchCriteria searchCriteria = UserSearchCriteria.builder()
                                        // Sanitize and set sorting parameters
                                        .sortBy(sanitizeSortField(sortBy))
                                        .sortDirection(sanitizeSortDirection(direction))

                                        // Sanitize pagination
                                        .page(sanitizePageNumber(pageNo) - 1) // Convert to zero-based index
                                        .size(sanitizePageSize(pageSize))

                                        // Add optional filters
                                        .status(status)
                                        .minCreatedAt(minCreatedAt)
                                        .maxCreatedAt(maxCreatedAt)
                                        .build();

                        // Retrieve users with validated criteria
                        return userService.getAllUsers(searchCriteria);
                })
                                .thenApply(allUsers -> ResponseEntity.ok(
                                                ApiResponseBuilder.createSuccessResponse(
                                                                allUsers,
                                                                ApiResponseStatus.RETRIEVE_ALL_USERS_SUCCESS)))
                                .exceptionally(ex -> {
                                        log.error("Error retrieving users", ex);

                                        if (ex.getCause() instanceof IllegalArgumentException) {
                                                return ResponseEntity.badRequest()
                                                                .body(ApiResponseBuilder.createErrorResponse(
                                                                                ApiResponseStatus.INVALID_PARAMETERS));
                                        }

                                        if (ex.getCause() instanceof SecurityException) {
                                                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                                                .body(ApiResponseBuilder.createErrorResponse(
                                                                                ApiResponseStatus.ACCESS_DENIED));
                                        }

                                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                                        .body(ApiResponseBuilder.createErrorResponse(
                                                                        ApiResponseStatus.INTERNAL_SERVER_ERROR));
                                });
        }

        @GetMapping("/search")
        @PreAuthorize("hasRole('ADMIN')")
        @Operation(summary = "Search users", description = "Performs advanced search on users")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Users found"),
                        @ApiResponse(responseCode = "403", description = "Access denied"),
        })
        public CompletableFuture<ResponseEntity<ApiResponseDTO<CompletableFuture<Page<User>>>>> searchUsers(
                        @Parameter(description = "Search query") @RequestParam(required = false) String searchQuery,

                        @Parameter(description = "User roles") @RequestParam(required = false) Set<Roles> roles,

                        @Parameter(description = "User status") @RequestParam(required = false) UserStatus status,

                        @Parameter(description = "Sort by field") @RequestParam(defaultValue = "userId") String sortBy,

                        @Parameter(description = "Sort order") @RequestParam(defaultValue = "asc") String direction,

                        @Parameter(description = "Page number") @RequestParam(defaultValue = "1") Integer pageNo,

                        @Parameter(description = "Page size") @RequestParam(defaultValue = "10") Integer pageSize,

                        @Parameter(description = "Minimum creation date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @RequestParam(required = false) LocalDateTime minCreatedAt,

                        @Parameter(description = "Maximum creation date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @RequestParam(required = false) LocalDateTime maxCreatedAt) {

                return CompletableFuture.supplyAsync(() -> {
                        // Comprehensive input validation and criteria building
                        UserSearchCriteria searchCriteria = UserSearchCriteria.builder()
                                        // Search parameters
                                        .searchQuery(searchQuery)
                                        .roles(roles)
                                        .status(status)

                                        // Sorting parameters
                                        .sortBy(sanitizeSortField(sortBy))
                                        .sortDirection(sanitizeSortDirection(direction))

                                        // Pagination
                                        .page(sanitizePageNumber(pageNo) - 1) // Convert to zero-based index
                                        .size(sanitizePageSize(pageSize))

                                        // Date range filters
                                        .minCreatedAt(minCreatedAt)
                                        .maxCreatedAt(maxCreatedAt)
                                        .build();

                        // Perform search with validated criteria
                        return userService.searchUsers(searchCriteria);
                })
                                .thenApply(searchResults -> ResponseEntity.ok(
                                                ApiResponseBuilder.createSuccessResponse(
                                                                searchResults,
                                                                ApiResponseStatus.RETRIEVE_SEARCH_USERS_SUCCESS)))
                                .exceptionally(ex -> {
                                        log.error("Error retrieving users", ex);

                                        if (ex.getCause() instanceof IllegalArgumentException) {
                                                return ResponseEntity.badRequest()
                                                                .body(ApiResponseBuilder.createErrorResponse(
                                                                                ApiResponseStatus.INVALID_PARAMETERS));
                                        }

                                        if (ex.getCause() instanceof SecurityException) {
                                                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                                                .body(ApiResponseBuilder.createErrorResponse(
                                                                                ApiResponseStatus.ACCESS_DENIED));
                                        }

                                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                                        .body(ApiResponseBuilder.createErrorResponse(
                                                                        ApiResponseStatus.INTERNAL_SERVER_ERROR));
                                });
        }

        // Sanitization methods
        private String sanitizeSortField(String sortBy) {
                return Optional.ofNullable(sortBy)
                                .filter(field -> ALLOWED_SORT_FIELDS.contains(field))
                                .orElse("userId");
        }

        private Sort.Direction sanitizeSortDirection(String direction) {
                try {
                        return Optional.ofNullable(direction)
                                        .map(String::toUpperCase)
                                        .filter(dir -> dir.equals("ASC") || dir.equals("DESC"))
                                        .map(Sort.Direction::valueOf)
                                        .orElse(Sort.Direction.ASC);
                } catch (IllegalArgumentException ex) {
                        return Sort.Direction.ASC;
                }
        }

        private int sanitizePageNumber(Integer pageNo) {
                return Optional.ofNullable(pageNo)
                                .filter(page -> page > 0)
                                .orElse(1);
        }

        private int sanitizePageSize(Integer pageSize) {
                return Optional.ofNullable(pageSize)
                                .filter(size -> size > 0 && size <= 100)
                                .orElse(10);
        }

        @DeleteMapping("/delete-user")
        @PreAuthorize("hasRole('ADMIN') and hasPermission(#userId, 'USER_DELETE')")
        public CompletableFuture<ResponseEntity<ApiResponseDTO<User>>> deleteUser(@RequestParam("userId") Long userId,
                        @RequestParam("username") String username) {

                if (userId == null || username == null) {
                        return CompletableFuture.completedFuture(
                                        ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                                        .body(ApiResponseBuilder.createErrorResponse(
                                                                        ApiResponseStatus.USER_NOT_FOUND)));
                }

                return CompletableFuture.supplyAsync(() -> {
                        // Pre-processing or additional validation can be done here
                        validateUser(userId, username);

                        return userService.deleteUsername(userId, username);
                })
                                .thenCompose(Function.identity()) // Flatten the CompletableFuture
                                .thenApply(user -> ResponseEntity.status(HttpStatus.OK)
                                                .body(ApiResponseBuilder.createSuccessResponse(user,
                                                                ApiResponseStatus.DELETE_USER_SUCCESSFULLY)))
                                .exceptionally(ex -> {
                                        if (ex.getCause() instanceof UserNotFoundException) {
                                                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                                                .body(ApiResponseBuilder.createErrorResponse(
                                                                                ApiResponseStatus.USER_NOT_FOUND));
                                        }

                                        if (ex.getCause() instanceof ValidationException) {
                                                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                                                .body(ApiResponseBuilder.createErrorResponse(
                                                                                ApiResponseStatus.INVALID_USER_ID));
                                        }

                                        // Log unexpected errors
                                        log.error("Unexpected error deleting user", ex);

                                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                                        .body(ApiResponseBuilder.createErrorResponse(
                                                                        ApiResponseStatus.FAILURE));
                                });
        }

        @DeleteMapping("/delete-user/{userId}")
        @PreAuthorize("hasRole('ADMIN') and hasPermission(#userId, 'USER_DELETE')")
        public CompletableFuture<ResponseEntity<ApiResponseDTO<User>>> deleteUserById(@PathVariable Long userId) {
                if (userId == null) {
                        return CompletableFuture.completedFuture(
                                        ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                                        .body(ApiResponseBuilder.createErrorResponse(
                                                                        ApiResponseStatus.USER_NOT_FOUND)));
                }

                return CompletableFuture.supplyAsync(() -> {
                        validateUserId(userId);
                        return userService.deleteUserById(userId);
                }).thenCompose(Function.identity()) // Flatten the CompletableFuture
                                .thenApply(user -> ResponseEntity.status(HttpStatus.OK)
                                                .body(ApiResponseBuilder.createSuccessResponse(user,
                                                                ApiResponseStatus.DELETE_USER_SUCCESSFULLY)))
                                .exceptionally(ex -> {
                                        if (ex.getCause() instanceof UserNotFoundException) {
                                                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                                                .body(ApiResponseBuilder.createErrorResponse(
                                                                                ApiResponseStatus.USER_NOT_FOUND));
                                        }

                                        if (ex.getCause() instanceof ValidationException) {
                                                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                                                .body(ApiResponseBuilder.createErrorResponse(
                                                                                ApiResponseStatus.INVALID_USER_ID));
                                        }

                                        // Log unexpected errors
                                        log.error("Unexpected error deleting user", ex);

                                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                                        .body(ApiResponseBuilder.createErrorResponse(
                                                                        ApiResponseStatus.FAILURE));
                                });

        }

        private void validateUser(Long userId, String username) {
                if (userId <= 0 || username.isEmpty()) {
                        throw new ValidationException("User name or user ID is invalid");
                }
        }

        private void validateUserId(Long userId) {
                if (userId <= 0) {
                        throw new ValidationException("Invalid user ID");
                }
        }
}
