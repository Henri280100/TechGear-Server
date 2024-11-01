package com.v01.techgear_server.controller.Users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.v01.techgear_server.dto.ApiResponse;
import com.v01.techgear_server.enums.ApiResponseStatus;
import com.v01.techgear_server.exception.UserNotFoundException;
import com.v01.techgear_server.model.User;
import com.v01.techgear_server.serviceImpls.UserServiceImpl;
import com.v01.techgear_server.utils.ApiResponseBuilder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v01/user")
public class UserController {

    @Autowired
    private UserServiceImpl userService;

    @GetMapping("/{user_id}")
    @PostAuthorize("(hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')) and hasPermission(returnObject, 'READ')")
    public ResponseEntity<ApiResponse<User>> getUserEntity(
            @PathVariable Long user_id) {

        if (user_id == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponseBuilder.createErrorResponse(ApiResponseStatus.USER_ID_NULL));
        }

        try {
            User foundUserDTO = userService.getUserById(user_id);
            return ResponseEntity.status(HttpStatus.OK).body(
                    ApiResponseBuilder.createSuccessResponse(foundUserDTO, ApiResponseStatus.RETRIEVE_USER_SUCCESS));
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseBuilder.createErrorResponse(ApiResponseStatus.ERROR_RETRIEVE_USER));
        }
    }
}
