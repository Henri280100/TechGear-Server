package com.v01.techgear_server.user.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ConfirmationTokensDTO {
    private Long confirmationTokensId;
    private String confirmToken;
    private LocalDateTime createdDate;
    private LocalDateTime expiryDate;
    private LocalDateTime confirmedAt;

    private UserDTO userDTO;
}
