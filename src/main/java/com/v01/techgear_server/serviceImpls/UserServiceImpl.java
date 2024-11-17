package com.v01.techgear_server.serviceImpls;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.v01.techgear_server.dto.UserSearchCriteria;
import com.v01.techgear_server.enums.UserStatus;
import com.v01.techgear_server.exception.BadRequestException;
import com.v01.techgear_server.exception.UserNotFoundException;
import com.v01.techgear_server.model.Image;
import com.v01.techgear_server.model.User;
import com.v01.techgear_server.repo.UserRepository;
import com.v01.techgear_server.service.FileStorageService;
import com.v01.techgear_server.service.UserService;
import com.v01.techgear_server.service.UserSpecifications;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;
    private final UserDetailsManager userDetailsManager;
    private final ExecutorService executorService;

    @Override
    public CompletableFuture<User> updateUsername(Long userId, String username) {
        return CompletableFuture.supplyAsync(() -> {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
            user.setUsername(username);
            return userRepository.save(user);
        }, executorService);
    }

    @Override
    public CompletableFuture<User> updateUserEmail(Long userId, String email) {
        return CompletableFuture.supplyAsync(() -> {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException("User  not found with email: " + email));
            user.setEmail(email);
            return userRepository.save(user);
        }, executorService);
    }

    @Override
    public CompletableFuture<User> getUserById(Long userId) {
        try {
            return CompletableFuture.supplyAsync(() -> userRepository.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException("User with ID " + userId + " not found")));
        } catch (UserNotFoundException e) {
            log.error("Error retrieving user with ID {}: {}", userId, e.getMessage());
            throw e;
        }
    }

    @Override
    public CompletableFuture<User> findUserByUsernameOrEmail(String username, String email) {
        return CompletableFuture.supplyAsync(() -> {
            // Validate inputs
            validateInputs(username, email);

            // Determine the identifier to use
            String identifier = determineIdentifier(username, email);

            // Use the repository method to find the user
            return userRepository.findByUsernameOrEmail(identifier)
                    .orElseThrow(() -> new UserNotFoundException(
                            "User not found with username: " + username + " or email: " + email));
        }, executorService);
    }

    // Comprehensive input validation
    private void validateInputs(String username, String email) {
        if ((username == null || username.isEmpty()) &&
                (email == null || email.isEmpty())) {
            throw new IllegalArgumentException("Both username and email cannot be null or empty");
        }
    }

    // Determine the most appropriate identifier
    private String determineIdentifier(String username, String email) {
        // Prioritize non-null input
        if (username != null && !username.isEmpty()) {
            return username;
        }
        if (email != null && !email.isEmpty()) {
            return email;
        }

        throw new IllegalArgumentException("No valid identifier provided");
    }

    @Override
    public CompletableFuture<User> deleteUserById(Long userId) {
        return CompletableFuture.supplyAsync(() -> {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException("User  not found with id " + userId));

            userRepository.deleteById(userId);

            return user;
        }).exceptionally(throwable -> {
            log.error("Error deleting user with ID {}: {}", userId, throwable.getMessage());
            throw new UserNotFoundException("User with ID " + userId + " not found", throwable);
        });
    }

    @Override
    public CompletableFuture<User> deleteUsername(Long userId, String username) {

        // Use UserDetailsManager to load the user
        UserDetails userDetails = userDetailsManager.loadUserByUsername(username);

        // Check if the provided userId matches the loaded user
        if (!userDetails.getUsername().equals(username)
                || !userId.equals(userRepository.findByUsername(username).get().getUserId())) {
            throw new BadRequestException("Username or User ID does not match the user to be deleted");
        }

        // Delete the user using UserDetailsManager
        userDetailsManager.deleteUser(username);

        // You might need to fetch and return the User entity from the repository
        // if you need additional information from the entity
        return CompletableFuture.supplyAsync(() -> userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found with username " + username)));

    }

    @Override
    public CompletableFuture<Page<User>> getAllUsers(UserSearchCriteria userSearchCriteria) {
        // Validate criteria
        userSearchCriteria.validate();

        // Create specification for dynamic filtering
        Specification<User> spec = createComprehensiveSpecification(userSearchCriteria);

        // Async execution with custom executor
        return CompletableFuture.supplyAsync(() -> userRepository.findAll(spec, userSearchCriteria.toPageable()),
                executorService);
    }

    @Override
    public CompletableFuture<Page<User>> searchUsers(UserSearchCriteria userSearchCriteria) {
        // Validate criteria
        userSearchCriteria.validate();

        // Create specification for search
        Specification<User> spec = createSearchSpecification(userSearchCriteria);

        // Async execution with custom executor
        return CompletableFuture.supplyAsync(() -> userRepository.findAll(spec, userSearchCriteria.toPageable()),
                executorService);
    }

    // Comprehensive specification for getAllUsers
    private Specification<User> createComprehensiveSpecification(UserSearchCriteria criteria) {
        Specification<User> spec = Specification.where(null);

        // Add roles specification
        if (criteria.getRoles() != null && !criteria.getRoles().isEmpty()) {
            spec = spec.and(UserSpecifications.hasRoles(criteria.getRoles()));
        }

        // Add status specification
        if (criteria.getStatus() != null) {
            spec = spec.and(createStatusSpecification(criteria.getStatus()));
        }

        // Add created at range specification
        if (criteria.getMinCreatedAt() != null || criteria.getMaxCreatedAt() != null) {
            spec = spec.and(createCreatedAtSpecification(
                    criteria.getMinCreatedAt(),
                    criteria.getMaxCreatedAt()));
        }

        return spec;
    }

    // Search specification with more comprehensive search
    private Specification<User> createSearchSpecification(UserSearchCriteria criteria) {
        Specification<User> spec = Specification.where(null);

        // Add search query specification (more comprehensive search)
        if (StringUtils.hasText(criteria.getSearchQuery())) {
            spec = spec.and(createAdvancedSearchSpecification(criteria.getSearchQuery()));
        }

        // Add roles specification
        if (criteria.getRoles() != null && !criteria.getRoles().isEmpty()) {
            spec = spec.and(UserSpecifications.hasRoles(criteria.getRoles()));
        }

        // Add permissions specification
        if (criteria.getPermissions() != null && !criteria.getPermissions().isEmpty()) {
            spec = spec.and(UserSpecifications.hasPermissions(criteria.getPermissions()));
        }

        // Add status specification
        if (criteria.getStatus() != null) {
            spec = spec.and(createStatusSpecification(criteria.getStatus()));
        }

        // Add created at range specification
        if (criteria.getMinCreatedAt() != null || criteria.getMaxCreatedAt() != null) {
            spec = spec.and(createCreatedAtSpecification(
                    criteria.getMinCreatedAt(),
                    criteria.getMaxCreatedAt()));
        }

        return spec;
    }

    // Advanced search specification
    private Specification<User> createAdvancedSearchSpecification(String searchQuery) {
        return (root, query, criteriaBuilder) -> {
            if (!StringUtils.hasText(searchQuery)) {
                return criteriaBuilder.conjunction();
            }

            String likePattern = "%" + searchQuery.toLowerCase() + "%";

            List<Predicate> predicates = new ArrayList<>();

            // Search across multiple fields
            predicates.add(criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("username")), likePattern),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), likePattern),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName")), likePattern),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("lastName")), likePattern)));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    // Status specification
    private Specification<User> createStatusSpecification(UserStatus status) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("status"), status);
    }

    // Created at range specification
    private Specification<User> createCreatedAtSpecification(
            LocalDateTime minCreatedAt,
            LocalDateTime maxCreatedAt) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (minCreatedAt != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("createdAt"),
                        minCreatedAt));
            }

            if (maxCreatedAt != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.get("createdAt"),
                        maxCreatedAt));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    @Override
    public User userUploadAvatar(User user, MultipartFile userAvatar) {
        validateAvatarFile(userAvatar);
        try {
            Image imageEntity = fileStorageService.uploadSingleImage(userAvatar);
            user.setUserAvatar(imageEntity);
            return userRepository.save(user);
        } catch (BadRequestException e) {
            log.error("BadRequestException: {}", e.getMessage());
            throw e;
        } catch (IOException e) {
            log.error("Exception occurred while uploading user avatar: {}", e.getMessage());
            throw new BadRequestException("Failed to upload user avatar");
        }
    }

    @Override
    public User userUpdateAvatar(User user, MultipartFile userAvatar) {
        // Validate the userAvatar file
        validateAvatarFile(userAvatar);

        // User user = userRepository.findById(user.getUserId())
        // .orElseThrow(() -> new UserNotFoundException("User with ID " +
        // user.getUserId() + " not found"));

        try {
            // Store the image using the fileStorageService
            Image imageEntity = fileStorageService.uploadSingleImage(userAvatar);

            // Update the user's avatar
            user.setUserAvatar(imageEntity);

            // Save and return the updated user entity
            return userRepository.save(user);

        } catch (IOException e) {
            log.error("IOException occurred while storing user avatar: {}", e.getMessage());
            throw new BadRequestException("Failed to upload user avatar. Please try again.");
        }
    }

    /**
     * Helper method to validate the avatar file.
     * 
     * @param userAvatar the file to be validated
     * @throws BadRequestException if the file is invalid
     */
    private void validateAvatarFile(MultipartFile userAvatar) {
        if (userAvatar == null || userAvatar.isEmpty()) {
            log.error("Invalid avatar file: Avatar file is null or empty");
            throw new BadRequestException("Image file is required");
        }

        // Optionally: you can add more validation here, such as file size or type check
        if (!isValidImageType(userAvatar)) {
            log.error("Invalid avatar file type: {}", userAvatar.getContentType());
            throw new BadRequestException("Invalid image file type. Only PNG, JPG, and JPEG are allowed.");
        }
    }

    /**
     * Method to check if the file type is a valid image.
     * 
     * @param userAvatar the file to be checked
     * @return true if valid, false otherwise
     */
    private boolean isValidImageType(MultipartFile userAvatar) {
        String contentType = userAvatar.getContentType();
        return contentType != null &&
                (contentType.equals("image/png") || contentType.equals("image/jpeg")
                        || contentType.equals("image/jpg"));
    }

}