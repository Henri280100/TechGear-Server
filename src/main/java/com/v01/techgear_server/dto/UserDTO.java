package com.v01.techgear_server.dto;

import java.util.List;

import lombok.Data;

@Data
public class UserDTO {
    private Long userId;
    private String userName;
    private List<EmailDTO> email;
    private String password;
    private List<ReviewsDTO> reviews;
    private List<UserPhoneNoDTO> phoneNumbers;
    private boolean active;
    private UserAddressDTO addresses;
    private List<String> passwordHistory;
    private List<RolesDTO> roles;
}
