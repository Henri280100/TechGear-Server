package com.v01.techgear_server.dto;

import lombok.Data;

@Data
public class UserPhoneNoDTO {
    private Long id;
    private String phoneNo;
    private String countryCode;
    private UserDTO user;
}
