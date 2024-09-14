package com.v01.techgear_server.dto;

import lombok.*;

@Getter
@Setter
public class ResetPasswordDTO {
    private String token;
    private String newPassword;
}
