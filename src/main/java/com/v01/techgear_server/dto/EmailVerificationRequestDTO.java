package com.v01.techgear_server.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailVerificationRequestDTO {
    @NotBlank(message = "Verification token is required")
    @Size(min = 10, max = 255, message = "Invalid token length")
    private String token;
}