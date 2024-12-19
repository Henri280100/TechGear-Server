package com.v01.techgear_server.dto;

import java.util.Set;
import com.v01.techgear_server.enums.UserStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User Data Transfer Object")
public class UserDTO {

    // Identification
    @Schema(description = "User ID", example = "123")
    @Positive(message = "User ID must be a positive number")
    private Long userId;

    // Authentication Credentials
    @Schema(description = "Username")
    @NotBlank(message = "Username must not be blank")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @Schema(description = "Email")
    @NotBlank(message = "Email must not be blank")
    @Email(message = "Email is not valid")
    @Size(max = 100, message = "Email must be less than 100 characters")
    private String email;

    // Security Attributes
    @Schema(description = "Users roles")
    private Set<String> roles;

    @Schema(description = "Active status of the user")
    private boolean isActive;

    @Schema(description = "Status of the user")
    private UserStatus userStatus;

    // Security Metadata
    @Schema(description = "Count of login attempts")
    @Min(value = 0, message = "Login attempts cannot be negative")
    @Max(value = 10, message = "Maximum login attempts exceeded")
    private int loginAttempts;

    // Associated Information
    @Schema(description = "Associated account details")
    private AccountDetailsDTO accountDetails;

    // Validation Methods
    public boolean isAccountNonLocked() {
        return loginAttempts < 5 && isActive;
    }

    public boolean isCredentialsNonExpired() {
        return userStatus == UserStatus.ACTIVE;
    }

}
