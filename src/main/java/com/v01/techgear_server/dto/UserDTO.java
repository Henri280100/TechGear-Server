package com.v01.techgear_server.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Set;
import java.time.*;
import com.v01.techgear_server.enums.UserGenders;
import com.v01.techgear_server.model.User;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User Data Transfer Object")
public class UserDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "Unique User Identifier")
    @Positive(message = "User ID must be a positive number")
    private Long userId;

    @Schema(description = "Username")
    @NotBlank(message = "Username cannot be blank")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @Schema(description = "User Password")
    @NotBlank(message = "Password cannot be blank")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;

    @Schema(description = "User Email")
    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Invalid email format")
    private String email;

    @Schema(description = "User Gender")
    private UserGenders genders;

    @Schema(description = "User Registration Date")
    private LocalDate registrationDate;

    @Schema(description="User date of birth")
    private LocalDateTime dateOfBirth;

    @Schema(description = "User Account Status")
    private boolean active;

    @Schema(description = "Account Creation Timestamp")
    private LocalDateTime createdAt;

    @Schema(description = "Last Updated Timestamp")
    private LocalDateTime updatedAt;

    @Schema(description = "User Reviews")
    private List<ReviewsDTO> reviews;

    @Schema(description = "User Phone Number")
    private UserPhoneNoDTO phoneNumbers;

    @Schema(description = "User Address")
    private UserAddressDTO addresses;

    @Schema(description = "Image associated with the user")
    private ImageDTO userAvatar;

    @Schema(description = "User Preferences")
    private UserPreferencesDTO preferences;

    @Schema(description = "User Metadata")
    private UserMetadataDTO metadata;

    // Transient fields for additional information
    @Schema(description = "Total Number of Reviews")
    private int totalReviews;

    @Schema(description = "Account Age in Days")
    private long accountAgeDays;

    // Builder method to create DTO from User entity
    public static UserDTO fromEntity(User user) {
        if (user == null) {
            return null;
        }

        return UserDTO.builder()
            .userId(user.getUserId())
            .username(user.getUsername())
            .password(user.getPassword()) // Be cautious with password handling
            .email(user.getEmail())
            .genders(user.getGenders())
            .active(user.isActive())
            .createdAt(user.getCreatedAt())
            .updatedAt(user.getUpdatedAt())
            .phoneNumbers(UserPhoneNoDTO.fromEntity(user.getPhoneNumbers()))
            .addresses(UserAddressDTO.fromEntity(user.getAddresses()))
            .userAvatar(ImageDTO.fromEntity(user.getUserAvatar()))
            .build();
    }

    public User toEntity() {
        User user = new User();
        user.setUserId(this.userId);
        user.setUsername(this.username);
        user.setPassword(this.password);
        user.setEmail(this.email);
        user.setGenders(this.genders);
        user.setActive(this.active);
        user.setCreatedAt(this.createdAt);
        user.setUpdatedAt(this.updatedAt);
        user.setPhoneNumbers(this.phoneNumbers != null ? 
            this.phoneNumbers.toEntity() : null);
        user.setAddresses(this.addresses != null ? 
            this.addresses.toEntity() : null);
        user.setUserAvatar(this.userAvatar != null ? 
            this.userAvatar.toEntity() : null);
        return user;
    }

    // Method to calculate account age
    public void calculateAccountAge() {
        if (createdAt != null) {
            this.accountAgeDays = java.time.Duration.between(
                createdAt, 
                LocalDateTime.now()
            ).toDays();
        }
    }

    // Method to calculate total reviews
    public void calculateTotalReviews() {
        this.totalReviews = reviews != null ? reviews.size() : 0;
    }

    // Validation method
    public boolean isValid() {
        return validateUsername() && 
               validateEmail() && 
               validatePassword();
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

    private boolean validatePassword() {
        return password != null && 
               password.length() >= 8 && 
               password.matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$");
    }

    // Additional DTO for User Preferences
    @Data
    @Builder
    public static class UserPreferencesDTO {
        private boolean darkMode;
        private String language;
        private boolean notificationsEnabled;
    }

    // Additional DTO for User Metadata
    @Data
    @Builder
    public static class UserMetadataDTO {
        private String lastLoginIp;
        private LocalDateTime lastLoginTime;
        private String deviceType;
        private String browser;
    }
}
