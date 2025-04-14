package com.v01.techgear_server.user.dto;

import java.util.Set;

import com.v01.techgear_server.enums.UserStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
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
    @Schema(description = "User roles")
    @NotEmpty(message = "User must have at least one role")
    private Set<String> userRoles;

    @Schema(description = "Active status of the user", example = "true")
    private boolean isActive; // ✅ Renamed for clarity

    @Schema(description = "User status", example = "ACTIVE")
    @NotNull(message = "User status cannot be null")
    private UserStatus userStatus;

    // Security Metadata
    @Schema(description = "Count of login attempts", example = "3")
    @Min(value = 0, message = "Login attempts cannot be negative")
    @Max(value = 10, message = "Maximum login attempts exceeded")
    private int loginAttempts;

    // Associated Information
    @Schema(description = "Associated account details")
    private AccountDetailsDTO accountDetails;

    // Validation Methods
    public boolean isAccountNonLocked() {
        return this.isActive && this.loginAttempts < 5;
    }

    public boolean isCredentialsNonExpired() {
        return this.userStatus == UserStatus.ACTIVE;
    }
}

