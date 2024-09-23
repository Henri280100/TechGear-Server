package com.v01.techgear_server.serviceImpls;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

import com.v01.techgear_server.dto.UserDTO;
import com.v01.techgear_server.dto.UserPhoneNoDTO;
import com.v01.techgear_server.enums.Roles;
import com.v01.techgear_server.exception.GenericException;
import com.v01.techgear_server.model.PasswordResetToken;
import com.v01.techgear_server.model.Role;
import com.v01.techgear_server.model.User;
import com.v01.techgear_server.repo.PasswordResetTokenRepository;
import com.v01.techgear_server.repo.RoleRepository;
import com.v01.techgear_server.repo.UserRepository;
import com.v01.techgear_server.utils.PasswordValidation;

@Service
public class AuthServiceImpl implements UserDetailsManager {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private UserPhoneNoServiceImpl userPhoneNumberService;
    // Service
    @Autowired
    private AddressServiceImpl addressService;

    @Autowired
    PasswordEncoder passwordEncoder;

    // @Autowired
    // EmailServiceImpl emailServiceImpl;

    @Autowired
    private ModelMapper modelMapper;

    private static Logger LOGGER = LoggerFactory.getLogger(AuthServiceImpl.class);

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User with username not found: " + username));

        if (!user.isActive()) {
            throw new IllegalArgumentException("Email not verified. Please verify your email.");
        }

        return user;
    }

    @Override
    public boolean userExists(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    @Override
    public void createUser(UserDetails user) {
        try {
            
            // Map UserDetails to UserDTO and User entity
            UserDTO userDTO = modelMapper.map(user, UserDTO.class);
            User users = modelMapper.map(userDTO, User.class);

            // Check if the user already exists by username or email
            if(isUsernameAvailable(users.getUsername())) {
                LOGGER.error("Username already exists");
                return;
            }

            // Validate user existence and password
            if (!PasswordValidation.isValidPassword(users.getPassword())) {
                throw new IllegalArgumentException("Password does not meet the security requirements");
            }

            if (isPasswordAvailable(user.getPassword())) {
                LOGGER.error("Password already exists");
                return;
            }

            if (userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
                throw new GenericException("Email " + userDTO.getEmail() + " already exists");
            }
            users.setUsername(users.getUsername());
            // Set encoded password
            users.setPassword(PasswordValidation.encodePassword(users.getPassword()));
            users.setGenders(userDTO.getGenders());

            // Assign roles to the user
            Set<Role> roles = assignRoles(userDTO);
            users.setRoles(roles);

            // Handle address if present
            addressService.handleUserAddress(userDTO, users);

            // Handle phone number if present
            handleUserPhoneNumbers(userDTO, users);

            LOGGER.info("User created successfully: {}", users);
        } catch (Exception e) {
            LOGGER.error("Exception occurred while creating user: {}", e.getMessage(), e);
            // ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to
            // create user");
        }
    }

    private Set<Role> assignRoles(UserDTO userDTO) {
        Set<Role> roles = new HashSet<>();

        // If no roles provided, assign the default USER role
        if (userDTO.getRoles() == null) {
            Role userRole = roleRepository.findByRoleType(Roles.USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role not found."));
            roles.add(userRole);
        } else {
            // Iterate over provided roles
            userDTO.getRoles().forEach(roleDTO -> {
                Role role = roleRepository.findByRoleType(roleDTO.getRoleType())
                        .orElseThrow(() -> new RuntimeException("Error: Role not found."));
                roles.add(role);
            });
        }
        return roles;
    }

    private void handleUserPhoneNumbers(UserDTO userDTO, User user) {
        if (userDTO.getPhoneNumbers() != null) {
            UserPhoneNoDTO phoneNumbers = userDTO.getPhoneNumbers();
            // phoneNumbers.setUser(userDTO); // Make sure to set the User reference in
            // UserPhoneNo
            userPhoneNumberService.saveUserPhoneNoDTO(userDTO, phoneNumbers);
        }
    }

    private boolean isUsernameAvailable(String username) {
        // Check if the username already exists in the database
        return userRepository.findByUsername(username).isPresent();
    }

    private boolean isPasswordAvailable(String newPassword) {
        // Check if the new password is already in the password history
        // Encode the new password and compare it with the password history
        // if it's OK then return true, if not return false.
        String hashedPassword = passwordEncoder.encode(newPassword);
        for (User user : userRepository.findAll()) {
            if (user.getPassword().contains(hashedPassword)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void updateUser(UserDetails user) {
        // try {
        // // TODO: Handle update logic here, currently only handle the userName and
        // // password fields,
        // // after the tested, I should handle other fields in the future.
        // // For now, just check if the user is an instance of User and check if
        // username,
        // // password exists.
        // if (!(user instanceof User)) {
        // throw new IllegalArgumentException("User must be an instance of User");
        // }

        // User updatedUser = (User) user;

        // // Fetch the existing user from the repository
        // User existingUser = userRepository.findByUserName(updatedUser.getUsername())
        // .orElseThrow(() -> new IllegalArgumentException(
        // "User with username " + updatedUser.getUsername() + " not found"));

        // // Update fields from the UserDetails
        // existingUser.setUserName(updatedUser.getUsername());
        // if (updatedUser.getPassword() != null &&
        // !updatedUser.getPassword().isEmpty()) {
        // existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        // }

        // // TODO: It should be handled email and roles assignment right here
        // // and the phoneNo and address should be handled in the future.

        // // Save the updated user
        // userRepository.save(existingUser);
        // ResponseEntity.status(HttpStatus.OK).body("User updated successfully");
        // } catch (Exception e) {
        // LOGGER.error("Exception occurred while updating user: {}", e.getMessage(),
        // e);
        // ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to
        // update user");
        // }

    }

    @Override
    public void deleteUser(String username) {
        if (!userExists(username)) {
            throw new IllegalArgumentException("User with username " + username + " not found");
        } else {
            userRepository.deleteByUsername(username);
        }

    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                throw new IllegalStateException("User is not authenticated");
            }

            String username = authentication.getName();

            if (!passwordEncoder.matches(oldPassword, userRepository.findByUsername(username)
                    .orElseThrow(() -> new IllegalStateException("User not found")).getPassword())) {
                throw new IllegalArgumentException("Old password is not correct");
            }

            if (!PasswordValidation.isValidPassword(newPassword)) {
                throw new IllegalArgumentException("New password is not meet the security requirements");
            }

            userRepository.updatePassword(username, passwordEncoder.encode(newPassword));
        } catch (Exception e) {
            LOGGER.error("Exception occurred while updating password: {}", e.getMessage(), e);
        }

    }

    public void onPasswordResetToken(UserDTO userDTO, String token) {
        User user = modelMapper.map(userDTO, User.class);
        PasswordResetToken passwordResetToken = new PasswordResetToken();
        passwordResetToken.setToken(token);
        passwordResetToken.setUser(user);
        passwordResetToken.setExpiredDate(LocalDateTime.now().plusHours(24)); // Token valid for 24 hours
        passwordResetTokenRepository.save(passwordResetToken);
    }

    public UserDTO findUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User's email not found"));
        return modelMapper.map(user, UserDTO.class);
    }

    public PasswordResetToken validatePasswordResetToken(String token) {
        return passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid token"));
    }

    public void updateUserPassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    // TODO: Login with a third party such as Google or Facebook

}
