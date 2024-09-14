package com.v01.techgear_server.controller.Users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.v01.techgear_server.repo.UserRepository;

@RestController
@RequestMapping("/api/v01/admin")
public class AdminController {

    @Autowired
    UserDetailsManager userDetailsManager;

    @Autowired
    UserRepository userRepository;

    @DeleteMapping("/delete-user")
    public ResponseEntity<String> deleteUser(@RequestParam("username") String username) {
        try {
            userDetailsManager.deleteUser(username);
            userRepository.deleteByUsername(username);
            return ResponseEntity.ok("User deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting user" + e.getMessage());
        }
    }
}
