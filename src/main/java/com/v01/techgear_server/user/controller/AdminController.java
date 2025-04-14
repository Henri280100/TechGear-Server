package com.v01.techgear_server.user.controller;

import java.util.concurrent.CompletableFuture;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.v01.techgear_server.common.dto.ApiResponseDTO;
import com.v01.techgear_server.user.dto.UserDTO;
import com.v01.techgear_server.user.mapping.UserMapper;
import com.v01.techgear_server.user.service.UserService;

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
	private final UserMapper userMapper;
	private final UserService userService;

	@GetMapping("/{userId}")
	@PreAuthorize("hasRole('ADMIN') and hasPermission(#userId, 'USER', 'USER_READ')")
	@Operation(summary = "Get a user by ID", description = "Returns the user with the specified ID")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "User found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDTO.class))),
			@ApiResponse(responseCode = "404", description = "User not found"),
	})
	public CompletableFuture<ResponseEntity<UserDTO>> getUserEntity(
			@Parameter(description = "ID of the user to retrieve", required = true) @PathVariable Long userId) {
		validateUserId(userId);
		return userService.getUserById(userId)
		                  .thenApply(ResponseEntity::ok)
		                  .exceptionally(ex -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
	}

	@GetMapping("/find-by-username-or-email")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Find user by username or email", description = "Returns the user with the specified username or email")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "User found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDTO.class))),
			@ApiResponse(responseCode = "404", description = "User not found"),
	})
	public CompletableFuture<ResponseEntity<UserDTO>> getUserByUsernameOrEmail(
			@Parameter(description = "Username or email of the user to retrieve", required = true) @RequestParam
			String username,
			@RequestParam String email) {
		// Validate input
		if (StringUtils.isEmpty(username) || StringUtils.isEmpty(email)) {
			return CompletableFuture.completedFuture(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null));
		}
		return userService
				.findUserByUsernameOrEmail(username, email)
				.thenApply(ResponseEntity::ok)
				.exceptionally(ex -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));

	}

	@DeleteMapping("/delete/{userId}")
	@PreAuthorize("hasRole('ADMIN') and hasPermission(#userId, 'USER_DELETE')")
	public CompletableFuture<ResponseEntity<UserDTO>> deleteUser(@PathVariable Long userId) {

		validateUserId(userId);

		return userService.deleteUserById(userId)
		                  .thenApply(ResponseEntity::ok)
		                  .exceptionally(ex -> ResponseEntity.status(HttpStatus.NOT_FOUND)
		                                                     .body(null));
	}



	@DeleteMapping("/delete-user/{userId}")
	@PreAuthorize("hasRole('ADMIN') and hasPermission(#userId, 'USER_DELETE')")
	public CompletableFuture<ResponseEntity<UserDTO>> deleteUserById(@PathVariable Long userId) {
		validateUserId(userId);

		return userService.deleteUserById(userId).thenApply(ResponseEntity::ok)
		                  .exceptionally(ex -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
	}

	private void validateUserId(Long userId) {
		if (userId <= 0) {
			throw new ValidationException("Invalid user ID");
		}
	}
}
