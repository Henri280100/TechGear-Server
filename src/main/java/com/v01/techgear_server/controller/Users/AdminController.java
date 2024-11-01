package com.v01.techgear_server.controller.Users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.v01.techgear_server.dto.ApiResponse;
import com.v01.techgear_server.enums.ApiResponseStatus;
import com.v01.techgear_server.model.User;
import com.v01.techgear_server.repo.UserRepository;
import com.v01.techgear_server.service.UserService;
import com.v01.techgear_server.utils.ApiResponseBuilder;

@RestController
@RequestMapping("/api/v01/admin")
public class AdminController {

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @DeleteMapping("/delete-user")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<User>> deleteUser(@RequestParam("user_id") Long user_id,
            @RequestParam("username") String username,
            @AuthenticationPrincipal User currentUser) {
        try {
            User user = userService.deleteUsername(user_id, username, currentUser);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(ApiResponseBuilder.createSuccessResponse(user, ApiResponseStatus.DELETE_USER_SUCCESSFULLY));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseBuilder.createErrorResponse(ApiResponseStatus.FAILURE));
        }
    }
}
