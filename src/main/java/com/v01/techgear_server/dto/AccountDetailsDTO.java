package com.v01.techgear_server.dto;

import java.util.HashSet;
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
    private String userFirstName;

    @Schema(description = "Last Name")
    @NotBlank(message = "Last name is required")
    private String userLastName;

    @Schema(description = "User Type")
    private UserTypes userType;

    @Schema(description = "Gender")
    private UserGenders userGender;

    @Schema(description = "Date of Birth")
    private LocalDateTime userDateOfBirth;

    @Schema(description = "Profile Image")
    private ImageDTO userProfileImage;

    @Schema(description = "Phone Numbers")
    private HashSet<UserPhoneNoDTO> userPhoneNoDTOS;

    @Schema(description = "User Addresses")
    private HashSet<UserAddressDTO> userAddressDTOS;
}