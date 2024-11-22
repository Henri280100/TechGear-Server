package com.v01.techgear_server.service;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import com.v01.techgear_server.dto.ImageDTO;
import com.v01.techgear_server.dto.UserDTO;
import com.v01.techgear_server.dto.UserProfileUpdateDTO;
import com.v01.techgear_server.dto.UserSearchCriteria;
import com.v01.techgear_server.enums.Roles;
import com.v01.techgear_server.model.Role;
import com.v01.techgear_server.model.User;

public interface UserService {

    CompletableFuture<User> getUserById(Long userId);
    CompletableFuture<User> updateUser(User user);
    CompletableFuture<User> deleteUserById(Long userId);
    CompletableFuture<User> deleteByUsername(Long userId, String username);

    // Username and Email Operations
    CompletableFuture<User> updateUsername(Long userId, String username);
    CompletableFuture<User> updateUserEmail(Long userId, String email);
    CompletableFuture<User> findUserByUsernameOrEmail(String username, String email);

    // Authentication and Security Related
    // CompletableFuture<User> enableTwoFactorAuthentication(Long userId);
    // CompletableFuture<User> disableTwoFactorAuthentication(Long userId);

    // Profile and Personal Information
    // CompletableFuture<User> updateUserProfile(Long userId, UserProfileUpdateDTO profileUpdateDTO);
    CompletableFuture<ImageDTO> uploadUserAvatar(MultipartFile userAvatar, UserDTO userDTO);
    CompletableFuture<ImageDTO> updateUserAvatar(Long userId, MultipartFile newUserAvatar, UserDTO userDTO);
    CompletableFuture<Void> deleteUserAvatar(Long userId);

    // User Roles and Permissions
    CompletableFuture<User> assignRole(Long userId, String roleName);
    CompletableFuture<User> removeRole(Long userId, String roleName);
    CompletableFuture<Boolean> hasRole(Long userId, String roleName);

    // Search and Filtering
    CompletableFuture<Page<User>> getAllUsers(UserSearchCriteria userSearchCriteria);
    CompletableFuture<Page<User>> searchUsers(UserSearchCriteria userSearchCriteria);
    CompletableFuture<List<User>> findUsersByRole(String roleName);

    // Account Management
    CompletableFuture<User> lockUser(Long userId);
    CompletableFuture<User> unlockUser(Long userId);
    CompletableFuture<Boolean> isUserLocked(Long userId);
    CompletableFuture<User> deactivateUser(Long userId);
    CompletableFuture<User> reactivateUser(Long userId);

    // Social and Connection Management
    CompletableFuture<List<User>> getUserConnections(Long userId);
    CompletableFuture<User> addUserConnection(Long userId, Long connectionUserId);
    CompletableFuture<Void> removeUserConnection(Long userId, Long connectionUserId);

    // Advanced User Queries
    CompletableFuture<List<User>> findInactiveUsers(int days);
    CompletableFuture<List<User>> findRecentlyRegisteredUsers(int days);

    // Compliance and Audit
    // CompletableFuture<List<UserAuditLogDTO>> getUserAuditLog(Long userId);
    CompletableFuture<Boolean> verifyUserConsent(Long userId, String consentType);

    // Additional DTO-based Operations
    CompletableFuture<UserDTO> getUserDetails(Long userId);
    // CompletableFuture<UserStatisticsDTO> getUserStatistics(Long userId);

    // Bulk Operations
    CompletableFuture<List<User>> bulkCreateUsers(List<User> users);
    CompletableFuture<List<User>> bulkUpdateUsers(List<User> users);
    CompletableFuture<Void> bulkDeleteUsers(List<Long> userIds);

    // Specialized Search Methods
    CompletableFuture<List<User>> findUsersByCountry(String country);
    CompletableFuture<List<User>> findUsersByAgeRange(int minAge, int maxAge);

    // Experimental or Advanced Features
    CompletableFuture<User> mergeUserProfiles(Long sourceUserId, Long targetUserId);
    CompletableFuture<List<User>> findPotentialConnections(Long userId);
}