package com.v01.techgear_server.dto;

import java.util.List;
import java.util.Set;

import com.v01.techgear_server.enums.UserGenders;

import lombok.Data;

@Data
public class UserDTO {

    private Long userId;
    private String username;
    private String password;
    private UserGenders genders;
    private String email;
    private List<ReviewsDTO> reviews;
    private UserPhoneNoDTO phoneNumbers;
    private boolean active;
    private UserAddressDTO addresses;
    private Set<RolesDTO> roles;

}
