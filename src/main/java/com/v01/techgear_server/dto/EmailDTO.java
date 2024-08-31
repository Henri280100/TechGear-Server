package com.v01.techgear_server.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class EmailDTO {
    private Long emailId;
    private String emailAddress;
    private String verificationToken;
    private boolean verified;
    private LocalDateTime sentAt;
    private LocalDateTime verifiedAt;
    private UserDTO user;
}
