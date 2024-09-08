package com.v01.techgear_server.serviceImpls;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.v01.techgear_server.dto.UserAddressDTO;
import com.v01.techgear_server.dto.UserDTO;
import com.v01.techgear_server.dto.UserPhoneNoDTO;
import com.v01.techgear_server.enums.Roles;
import com.v01.techgear_server.model.Role;
import com.v01.techgear_server.model.User;
import com.v01.techgear_server.model.UserAddress;
import com.v01.techgear_server.repo.RoleRepository;
import com.v01.techgear_server.repo.UserAddressRepository;
import com.v01.techgear_server.repo.UserRepository;
import com.v01.techgear_server.service.MapBoxService;
import com.v01.techgear_server.utils.PasswordValidation;

@Service
public class AuthServiceImpl implements UserDetailsManager {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserAddressRepository addressRepository;

    @Autowired
    private UserPhoneNoServiceImpl userPhoneNumberService;
    // Service
    @Autowired
    private MapBoxService mapBoxService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailServiceImpl emailServiceImpl;

    @Autowired
    private ModelMapper modelMapper;

    private static Logger LOGGER = LoggerFactory.getLogger(AuthServiceImpl.class);

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User with username not found: " + username));

        if(!user.isActive()) {
            throw new IllegalArgumentException("Email not verified for user" + username);
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
            if (userRepository.existsByUsername(users.getUsername())) {
                LOGGER.error("Username already exists.");
            }
            // Validate user existence and password
            if (!PasswordValidation.isValidPassword(user.getPassword())) {
                throw new IllegalArgumentException("Password does not meet the security requirements");
            }

            if (isPasswordAvailable(user.getPassword())) {
                LOGGER.error("Password already exists");
            }

            // Set encoded password
            users.setPassword(passwordEncoder.encode(user.getPassword()));

            // Assign roles to the user
            Set<Role> roles = assignRoles(userDTO);
            users.setRoles(roles);

            // Handle address if present
            handleUserAddress(userDTO, users);

            // Handle phone number if present
            handleUserPhoneNumbers(userDTO, users);

            // Save user and trigger email verification
            User saveUser = userRepository.save(users);

            modelMapper.map(saveUser, UserDTO.class);
            emailServiceImpl.sendVerificationEmail(userDTO);

            LOGGER.info("User created successfully: {}", saveUser);
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

    private UserAddress handleUserAddress(UserDTO userDTO, User user) {
        if (userDTO.getAddresses() == null) {
            return null; // No address provided
        }

        // Get the address DTO from userDTO
        UserAddressDTO addressDTO = userDTO.getAddresses();

        // Check if the address already exists in the database
        Optional<UserAddress> existingAddress = addressRepository.findByAddressDetails(addressDTO.getAddressDetails());

        if (existingAddress.isPresent()) {
            // If the address exists, return it and set it to the user
            UserAddress address = existingAddress.get();
            user.setAddresses(address);
            return address;
        }

        // Otherwise, create a new address entity
        UserAddress newAddress = modelMapper.map(addressDTO, UserAddress.class);

        // Geocode the address if address details are provided
        if (addressDTO.getAddressDetails() != null && !addressDTO.getAddressDetails().isEmpty()) {
            CarmenFeature geocodedFeature = mapBoxService.geocodeFeature(addressDTO.getAddressDetails());
            if (geocodedFeature != null) {
                newAddress.setLatitude(geocodedFeature.center().latitude());
                newAddress.setLongitude(geocodedFeature.center().longitude());
            }
        }

        // Save the new address to the database
        UserAddress savedAddress = addressRepository.save(newAddress);

        // Set the saved address to the user
        user.setAddresses(savedAddress);

        return savedAddress;
    }

    private void handleUserPhoneNumbers(UserDTO userDTO, User user) {
        if (userDTO.getPhoneNumbers() != null) {
            UserPhoneNoDTO phoneNumbers = userDTO.getPhoneNumbers();
            // phoneNumbers.setUser(userDTO); // Make sure to set the User reference in
            // UserPhoneNo
            userPhoneNumberService.saveUserPhoneNoDTO(userDTO, phoneNumbers);
        }
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
            ResponseEntity.status(HttpStatus.OK).body("Password updated successfully");
        } catch (Exception e) {
            LOGGER.error("Exception occurred while updating password: {}", e.getMessage(), e);
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update password");
        }

    }

}
