package com.v01.techgear_server.controller.Users;

import java.util.List;

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
import com.v01.techgear_server.enums.ApiResponseStatus;
import com.v01.techgear_server.exception.UserNotFoundException;
import com.v01.techgear_server.model.User;
import com.v01.techgear_server.repo.UserRepository;
import com.v01.techgear_server.service.UserService;
import com.v01.techgear_server.utils.ApiResponseBuilder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v01/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final UserRepository userRepository;

    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN') and hasPermission(#userId, 'USER', 'USER_READ')")
    public ResponseEntity<ApiResponseDTO<User>> getUserEntity(@PathVariable Long userId) {
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponseBuilder.createErrorResponse(ApiResponseStatus.USER_ID_NULL));
        }

        try {
            User foundUserDTO = userService.getUserById(userId);
            return ResponseEntity.status(HttpStatus.OK).body(
                    ApiResponseBuilder.createSuccessResponse(foundUserDTO, ApiResponseStatus.RETRIEVE_USER_SUCCESS));
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseBuilder.createErrorResponse(ApiResponseStatus.ERROR_RETRIEVE_USER));
        }
    }

    @GetMapping("/all-users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<List<User>>> getAllUsers(
            @RequestParam(value = "sortBy", required = false, defaultValue = "userId") String sortBy,
            @RequestParam(value = "direction", required = false, defaultValue = "asc") String direction) {
        try {
            List<User> allUsers = userService.getAllUsers(sortBy, direction);
            return ResponseEntity.status(HttpStatus.OK).body(
                    ApiResponseBuilder.createSuccessResponse(allUsers, ApiResponseStatus.RETRIEVE_ALL_USERS_SUCCESS));
        } catch (Exception e) {
            log.error("Error in getAllUsers: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseBuilder.createErrorResponse(ApiResponseStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @DeleteMapping("/delete-user")
    @PreAuthorize("hasRole('ADMIN') and hasPermission(#userId, 'USER_DELETE')")
    public ResponseEntity<ApiResponseDTO<User>> deleteUser(@RequestParam("userId") Long userId,
            @RequestParam("username") String username) {
        try {
            User user = userService.deleteUsername(userId, username);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(ApiResponseBuilder.createSuccessResponse(user, ApiResponseStatus.DELETE_USER_SUCCESSFULLY));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseBuilder.createErrorResponse(ApiResponseStatus.FAILURE));
        }
    }

    @DeleteMapping("/delete-user/{userId}")
    @PreAuthorize("hasRole('ADMIN') and hasPermission(#userId, 'USER_DELETE')")
    public ResponseEntity<ApiResponseDTO<User>> deleteUserById(@PathVariable Long userId) {
        try {
            User user = userService.deleteUserById(userId);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(ApiResponseBuilder.createSuccessResponse(user, ApiResponseStatus.DELETE_USER_SUCCESSFULLY));
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseBuilder.createErrorResponse(ApiResponseStatus.ERROR_RETRIEVE_USER));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseBuilder.createErrorResponse(ApiResponseStatus.FAILURE));
        }
    }
}
