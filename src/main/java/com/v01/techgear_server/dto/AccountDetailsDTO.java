package com.v01.techgear_server.dto;

import java.time.LocalDateTime;
import com.v01.techgear_server.enums.UserGenders;
import com.v01.techgear_server.enums.UserTypes;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Account Details Data Transfer Object")
public class AccountDetailsDTO {
    @Schema(description = "Account ID")
    @Positive(message = "Account ID must be positive")
    private Long accountDetailsId;

    @Schema(description = "First Name")
    @NotBlank(message = "First name is required")
    private String firstName;

    @Schema(description = "Last Name")
    @NotBlank(message = "Last name is required")
    private String lastName;

    @Schema(description = "User Type")
    private UserTypes userType;

    @Schema(description = "Gender")
    private UserGenders gender;

    @Schema(description = "Date of Birth")
    @Past(message = "Date of birth must be in the past")
    private LocalDateTime dateOfBirth;

    @Schema(description = "Phone Number")
    private UserPhoneNoDTO userPhoneNo;

    @Schema(description = "User Address")
    private UserAddressDTO userAddress;
}