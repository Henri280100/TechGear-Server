package com.v01.techgear_server.dto;

import java.util.Set;

import com.v01.techgear_server.enums.AuthProvider;
import com.v01.techgear_server.enums.UserStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
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
    
    @Schema(description = "User ID", example = "123")
    @Positive(message = "User ID must be a positive number")
    private Long userId;
    
    @Schema(description = "Username")
    @NotBlank(message = "Username must not be blank")
    private String username;
    
    @Schema(description = "Password", hidden = true)
    @NotBlank(message = "Password must not be blank")
    private String password;
    
    @Schema(description = "Email")
    @Email(message = "Email is not valid")
    private String email;
    
    @Schema(description = "Authentication provider")
    private AuthProvider provider;
    
    @Schema(description = "Account details associated with the user")
    private AccountDetailsDTO accountDetails;
    
    @Schema(description = "Confirmation tokens associated with the user")
    private ConfirmationTokensDTO confirmationTokens;
    
    @Schema(description = "Roles associated with the user")
    private Set<RoleDTO> roles;
    
    @Schema(description = "Is the user active")
    private boolean active;
    
    @Schema(description = "Number of login attempts")
    private int loginAttempts;
    
    @Schema(description = "User status")
    private UserStatus userStatus;
    
}
