package com.v01.techgear_server.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.util.StringUtils;

import com.v01.techgear_server.enums.UserGenders;
import com.v01.techgear_server.model.Image;
import com.v01.techgear_server.model.User;
import com.v01.techgear_server.model.UserAddress;

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
@Schema(description = "User profile update details")
public class UserProfileUpdateDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "User ID", example = "123")
    private Long userId;

    @Schema(description = "Username")
    @NotBlank(message = "Username cannot be blank")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @Schema(description = "Email Address")
    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Invalid email format")
    private String email;

    @Schema(description = "Gender")
    @NotNull(message = "Gender must be specified")
    private UserGenders genders;

    @Schema(description = "Date of Birth")
    @PastOrPresent(message = "Date of birth must be in the past or present")
    private LocalDateTime dateOfBirth;

    @Schema(description = "Phone Number")
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number")
    private String phoneNumber;

    @Schema(description = "User Avatar")
    private Image userAvatar;

    @Schema(description = "User Address")
    private UserAddress addresses;

    @Builder.Default
    @Schema(description = "Additional Profile Information")
    private Map<String, String> additionalInfo = new HashMap<>();

    @Schema(description = "Profile Completion Percentage")
    @Min(0)
    @Max(100)
    private Integer profileCompletionPercentage;

    // Method to convert User entity to UserProfileUpdateDTO
    public static UserProfileUpdateDTO fromEntity(User user) {
        return UserProfileUpdateDTO.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .genders(user.getGenders())
                .dateOfBirth(user.getDateOfBirth())
                .phoneNumber(user.getPhoneNumbers() != null ? user.getPhoneNumbers().getPhoneNo() : null)
                .userAvatar(user.getUserAvatar())
                .addresses(user.getAddresses())
                .build();
    }

    // Method to update User entity
    public User toEntity(User existingUser) {
        existingUser.setUsername(this.username);
        existingUser.setEmail(this.email);
        existingUser.setGenders(this.genders);
        existingUser.setDateOfBirth(this.dateOfBirth);

        // Update phone number if exists
        if (existingUser.getPhoneNumbers() != null) {
            existingUser.getPhoneNumbers().setPhoneNo(this.phoneNumber);
        }

        // Update avatar if provided
        if (this.userAvatar != null) {
            existingUser.setUserAvatar(this.userAvatar);
        }

        // Update address if provided
        if (this.addresses != null) {
            existingUser.setAddresses(this.addresses);
        }

        return existingUser;
    }

    // Method to calculate profile completion
    public void calculateProfileCompletion() {
        int completionScore = 0;

        if (StringUtils.hasText(username))
            completionScore += 20;
        if (StringUtils.hasText(email))
            completionScore += 20;
        if (genders != null)
            completionScore += 15;
        if (dateOfBirth != null)
            completionScore += 15;
        if (StringUtils.hasText(phoneNumber))
            completionScore += 10;
        if (userAvatar != null)
            completionScore += 10;
        if (addresses != null)
            completionScore += 10;

        this.profileCompletionPercentage = Math.min(completionScore, 100);
    }

    // Validation method
    public boolean isValid() {
        return validateUsername() &&
                validateEmail() &&
                validateDateOfBirth();
    }

    private boolean validateUsername() {
        return username != null &&
                username.length() >= 3 &&
                username.length() <= 50;
    }

    private boolean validateEmail() {
        return email != null &&
                email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    private boolean validateDateOfBirth() {
        return dateOfBirth == null ||
                dateOfBirth.isBefore(LocalDateTime.now());
    }
}
