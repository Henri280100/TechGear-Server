package com.v01.techgear_server.user.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.web.multipart.MultipartFile;

import com.v01.techgear_server.user.dto.AccountDetailsDTO;
import com.v01.techgear_server.common.dto.ImageDTO;
import com.v01.techgear_server.user.dto.UserAddressDTO;
import com.v01.techgear_server.user.dto.UserDTO;
import com.v01.techgear_server.user.dto.UserPhoneNoDTO;

public interface UserService {

    CompletableFuture<UserDTO> getUserById(Long userId);

    CompletableFuture<UserDTO> deleteUserById(Long userId);

    CompletableFuture<UserDTO> deleteByUsername(Long userId, String username);

    CompletableFuture<UserAddressDTO> createUserNewAddress(AccountDetailsDTO accountDetailsDTO);

    CompletableFuture<UserAddressDTO> updateUserAddress(Long userAddressId, UserAddressDTO userAddressDTO);

    CompletableFuture<UserPhoneNoDTO> saveUserPhoneNo(AccountDetailsDTO accountDetailsDTO);

    // Username and Email Operations
    CompletableFuture<UserDTO> updateUsername(Long userId, String username);

    CompletableFuture<UserDTO> updateUserEmail(Long userId, String email);

    CompletableFuture<UserDTO> findUserByUsernameOrEmail(String username, String email);

    CompletableFuture<ImageDTO> uploadUserAvatar(MultipartFile userAvatar, AccountDetailsDTO accountDetailsDTO);

    CompletableFuture<ImageDTO> updateUserAvatar(Long accountDetailsId, MultipartFile newUserAvatar,
            AccountDetailsDTO accountDetailsDTO);

    // User Roles and Permissions
    CompletableFuture<UserDTO> assignRole(Long userId, String roleName);

    CompletableFuture<UserDTO> removeRole(Long userId, String roleName);

    CompletableFuture<Boolean> hasRole(Long userId, String roleName);

    CompletableFuture<List<UserDTO>> findUsersByRole(String roleName, String status);

    // Account Management
    CompletableFuture<UserDTO> lockUser(Long userId);

    CompletableFuture<UserDTO> unlockUser(Long userId);

    CompletableFuture<Boolean> isUserLocked(Long userId);

    CompletableFuture<UserDTO> deactivateUser(Long userId);

    CompletableFuture<UserDTO> reactivateUser(Long userId);

    // Social and Connection Management
    CompletableFuture<List<UserDTO>> getUserConnections(Long userId);

    CompletableFuture<UserDTO> addUserConnection(Long userId, Long connectionUserId);

    CompletableFuture<Void> removeUserConnection(Long userId, Long connectionUserId);

    // Advanced User Queries
    CompletableFuture<List<UserDTO>> findInactiveUsers(int days);

    CompletableFuture<List<UserDTO>> findRecentlyRegisteredUsers(int days);

    // Bulk Operations
    CompletableFuture<List<UserDTO>> bulkCreateUsers(List<UserDTO> userDTO);

    CompletableFuture<List<UserDTO>> bulkUpdateUsers(List<UserDTO> userDTO);

    CompletableFuture<Void> bulkDeleteUsers(List<Long> userIds);

    // Specialized Search Methods
    CompletableFuture<List<UserAddressDTO>> findUsersByCountry(String country);

    CompletableFuture<List<UserDTO>> findUsersByAgeRange(int minAge, int maxAge);

    CompletableFuture<List<UserDTO>> findPotentialConnections(Long userId);



}