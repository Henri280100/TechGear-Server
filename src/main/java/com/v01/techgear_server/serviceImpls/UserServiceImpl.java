package com.v01.techgear_server.serviceImpls;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.v01.techgear_server.enums.Roles;
import com.v01.techgear_server.exception.BadRequestException;
import com.v01.techgear_server.exception.UserNotFoundException;
import com.v01.techgear_server.model.Image;
import com.v01.techgear_server.model.Role;
import com.v01.techgear_server.model.User;
import com.v01.techgear_server.repo.UserRepository;
import com.v01.techgear_server.service.FileStorageService;
import com.v01.techgear_server.service.UserService;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private UserDetailsManager userDetailsManager;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    public User updateUsername(Long userId, String username) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User  not found with username: " + username));
        user.setUsername(username);
        
        return user;

    }

    @Override
    public User updateUserEmail(Long userId, String email) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User  not found with email: " + email));
        user.setEmail(email);
        
        return user;

    }

    @Override
    public User getUserById(Long userId) {
        try {
            return userRepository.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException("User with ID " + userId + " not found"));
        } catch (UserNotFoundException e) {
            LOGGER.error("Error retrieving user with ID {}: {}", userId, e.getMessage());
            throw e;
        }
    }

    @Override
    public User deleteUserById(Long userId, User currentUser) {
        isUserAdmin(currentUser.getUser_id(), currentUser.getRoles());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User  not found with id " + userId));
        userRepository.deleteById(userId);
        
        return user;

    }

    @Override
    public User deleteUsername(Long userId, String username, User currentUser) {
        isUserAdmin(currentUser.getUser_id(), currentUser.getRoles());
        // Use UserDetailsManager to load the user
        UserDetails userDetails = userDetailsManager.loadUserByUsername(username);

        // Check if the provided userId matches the loaded user
        if (!userDetails.getUsername().equals(username) || !userId.equals(userRepository.findByUsername(username).get().getUser_id())) {
            throw new BadRequestException("Username or User ID does not match the user to be deleted");
        }

        // Delete the user using UserDetailsManager
        userDetailsManager.deleteUser(username); 

        // You might need to fetch and return the User entity from the repository 
        // if you need additional information from the entity 
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found with username " + username));

    }

    @Override
    public List<User> getAllUsersSorted(String sortBy, String direction, User currentUser) {
        isUserAdmin(currentUser.getUser_id(), currentUser.getRoles());
        // Create a Sort object based on the provided field and direction
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        List<User> users = userRepository.findAll(sort);
        
        return users;
    }

    

    @Override
    public List<User> getAllUsersNoSorted(User currentUser) {
        isUserAdmin(currentUser.getUser_id(), currentUser.getRoles());
        List<User> users = userRepository.findAll();
        return users;
    }

    @Override
    public User userUploadAvatarHandler(User user, MultipartFile userAvatar) {
        validateAvatarFile(userAvatar);
        try {
            Image imageEntity = fileStorageService.uploadSingleImage(userAvatar);
            user.setUserAvatar(imageEntity);
            return userRepository.save(user);
        } catch (BadRequestException e) {
            LOGGER.error("BadRequestException: {}", e.getMessage());
            throw e;
        } catch (IOException e) {
            LOGGER.error("Exception occurred while uploading user avatar: {}", e.getMessage());
            throw new BadRequestException("Failed to upload user avatar");
        }
    }

    @Override
    public User userUpdateAvatarHandler(User user, MultipartFile userAvatar) {
        // Validate the userAvatar file
        validateAvatarFile(userAvatar);

        // User user = userRepository.findById(user.getUser_id())
        // .orElseThrow(() -> new UserNotFoundException("User with ID " +
        // user.getUser_id() + " not found"));

        try {
            // Store the image using the fileStorageService
            Image imageEntity = fileStorageService.uploadSingleImage(userAvatar);

            // Update the user's avatar
            user.setUserAvatar(imageEntity);

            // Save and return the updated user entity
            return userRepository.save(user);

        } catch (IOException e) {
            LOGGER.error("IOException occurred while storing user avatar: {}", e.getMessage());
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
            LOGGER.error("Invalid avatar file: Avatar file is null or empty");
            throw new BadRequestException("Image file is required");
        }

        // Optionally: you can add more validation here, such as file size or type check
        if (!isValidImageType(userAvatar)) {
            LOGGER.error("Invalid avatar file type: {}", userAvatar.getContentType());
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

    private User isUserAdmin(Long userId, Set<Role> roles) {
        // Check if any of the user's roles is ADMIN
        boolean isAdmin = roles.stream()
                .anyMatch(role -> role.getRoleType() == Roles.ROLE_ADMIN);

        if (!isAdmin) {
            // Throw an exception or handle unauthorized access
            throw new SecurityException("Access denied. You must be an admin to perform this action.");
        }

        // Retrieve the user object based on userId
        return getUserById(userId);
    }
}