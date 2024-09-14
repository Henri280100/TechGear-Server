package com.v01.techgear_server.controller.Users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.v01.techgear_server.model.User;
import com.v01.techgear_server.serviceImpls.UserServiceImpl;

@RestController
@RequestMapping("/api/v01/users")
public class UserController {
    
    @Autowired
    private UserServiceImpl userService;

    @GetMapping("/{id}")
    @PreAuthorize("#user.id == #id")
    public ResponseEntity<?> getUserEntity(@AuthenticationPrincipal User user, @PathVariable Long id) {
        User foundUser = userService.getUserById(id);
        return ResponseEntity.ok(foundUser);
    }
}
