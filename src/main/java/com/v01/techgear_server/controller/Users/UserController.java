package com.v01.techgear_server.controller.Users;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.v01.techgear_server.dto.ApiResponseDTO;
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

    
}
