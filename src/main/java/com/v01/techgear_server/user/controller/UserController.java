package com.v01.techgear_server.user.controller;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import com.v01.techgear_server.shared.dto.ApiResponseDTO;
import com.v01.techgear_server.shared.dto.ImageDTO;
import com.v01.techgear_server.user.dto.AccountDetailsDTO;
import com.v01.techgear_server.user.dto.UserAddressDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.v01.techgear_server.enums.ApiResponseStatus;
import com.v01.techgear_server.exception.UserNotFoundException;
import com.v01.techgear_server.user.service.UserService;
import com.v01.techgear_server.utils.ApiResponseBuilder;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.*;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v01/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

@PutMapping("/{userId}/profile/avatar")
@Operation(summary = "Update user's avatar", description = "Performs the update of user's avatar")
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User's avatar updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Bad request due to invalid input", content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "404", description = "User not found", content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json"))
})
public CompletableFuture<ResponseEntity<ApiResponseDTO<ImageDTO>>> updateAvatar(
        @Parameter(description = "User ID", required = true) @PathVariable Long userId,
        @Parameter(description = "User avatar file", required = true) @RequestPart MultipartFile userAvatar)
        throws UserNotFoundException {

    if (userId == null || userAvatar.isEmpty()) {
        return CompletableFuture.completedFuture(
                ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponseBuilder.createErrorResponse(ApiResponseStatus.INVALID_INPUT)));
    }

    return CompletableFuture.supplyAsync(() -> {
        validateUser(userId);
        return userService.updateUserAvatar(userId, userAvatar, new AccountDetailsDTO());
    }).thenCompose(Function.identity())
    .thenApply(imageDTO -> ResponseEntity.status(HttpStatus.OK).body(ApiResponseBuilder.createSuccessResponse(imageDTO, ApiResponseStatus.AVATAR_UPDATED)))
    .exceptionally(ex -> {
        if (ex.getCause() instanceof UserNotFoundException) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseBuilder.createErrorResponse(ApiResponseStatus.USER_NOT_FOUND));
        }
        // Log unexpected errors
        log.error("Error updating user avatar", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseBuilder.createErrorResponse(ApiResponseStatus.INTERNAL_SERVER_ERROR));
    });
}

    @PutMapping("/{userId}/profile/address")
    @Operation(summary = "Update user's address", description = "Performs the update of user's address")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User's address updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Bad request due to invalid input", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json"))
    })
    public CompletableFuture<ResponseEntity<ApiResponseDTO<UserAddressDTO>>> updateAddress(
            @Parameter(description = "User ID", required = true) @PathVariable Long userId,
            @Parameter(description = "User address data", required = true) @RequestBody UserAddressDTO userAddressDTO)
            throws UserNotFoundException {

        if (userId == null) {
            return CompletableFuture.completedFuture(
                    ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(ApiResponseBuilder.createErrorResponse(ApiResponseStatus.USER_NOT_FOUND)));
        }

        return CompletableFuture.supplyAsync(() -> {
            validateUser(userId);
            return userService.updateUserAddress(userId, userAddressDTO);
        }).thenCompose(Function.identity())
                .thenApply(addressDTO -> ResponseEntity.status(HttpStatus.OK)
                        .body(ApiResponseBuilder.createSuccessResponse(addressDTO,
                                ApiResponseStatus.UPDATE_USER_ADDRESS_SUCCESSFULLY)))
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
                    log.error("Unexpected error while updating user", ex);

                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(ApiResponseBuilder.createErrorResponse(
                                    ApiResponseStatus.FAILURE));
                });
    }

    private void validateUser(Long userId) {
        if (userId <= 0) {
            throw new ValidationException("Invalid user ID");
        }
    }
}
