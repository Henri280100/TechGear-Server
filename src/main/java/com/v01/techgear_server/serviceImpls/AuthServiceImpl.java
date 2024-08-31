package com.v01.techgear_server.serviceImpls;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
import com.v01.techgear_server.dto.RolesDTO;
import com.v01.techgear_server.dto.UserAddressDTO;
import com.v01.techgear_server.dto.UserDTO;
import com.v01.techgear_server.dto.UserPhoneNoDTO;
import com.v01.techgear_server.enums.Roles;
import com.v01.techgear_server.model.Role;
import com.v01.techgear_server.model.User;
import com.v01.techgear_server.model.UserAddress;
import com.v01.techgear_server.repo.RoleRepository;
import com.v01.techgear_server.repo.UserRepository;
import com.v01.techgear_server.service.MapBoxService;
import com.v01.techgear_server.serviceImpls.Email.EmailServiceImpl;
import com.v01.techgear_server.utils.PasswordValidation;

import jakarta.transaction.Transactional;

@Service
public class AuthServiceImpl implements UserDetailsManager {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

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
        // Wtf... only check the username.
        Optional<User> userOptional = userRepository.findByUserName(username);

        if (userOptional.isEmpty()) {
            throw new UsernameNotFoundException(MessageFormat.format("User with username {0} not found", username));
        }
        return userOptional.get();
    }

    @Override
    public boolean userExists(String username) {
        return userRepository.findByUserName(username).isPresent();
    }

    @SuppressWarnings("unchecked")
    @Transactional
    @Override
    public void createUser(UserDetails user) {

        try {
            UserDTO userDTO = (UserDTO) user;
            User newUser = modelMapper.map(userDTO, User.class);
            List<Role> roles = new ArrayList<>();

            if (userExists(user.getUsername())) {
                throw new IllegalArgumentException("User with username " + user.getUsername() + " already exists");
            }

            if (!PasswordValidation.isValidPassword(user.getPassword())) {
                throw new IllegalArgumentException("Password is not meet the security requirements");
            }

            if (isPasswordAvailable(user.getPassword())) {
                throw new IllegalArgumentException("Password is already available");
            }

            newUser.setPassword(passwordEncoder.encode(user.getPassword()));

            // Handle user roles
            if (userDTO.getRoles() != null) {
                for (RolesDTO roleDTO : userDTO.getRoles()) {
                    Role role = modelMapper.map(roleDTO, Role.class);
                    role.getRoleType();
                    // find and save role
                    roles.add(roleRepository.findByRoleType(Roles.ROLE_USER));
                }
                newUser.setRoles(roles);
            }

            // Handle phone number if present
            if (userDTO.getPhoneNumbers() != null) {
                for (UserPhoneNoDTO phoneNoDTO : userDTO.getPhoneNumbers()) {
                    phoneNoDTO.setId(newUser.getUserId()); // Set the user ID
                    userPhoneNumberService.saveUserPhoneNoDTO((List<UserPhoneNoDTO>) phoneNoDTO); // Save phone number
                }
            }

            // Handle user address
            if (userDTO.getAddresses() != null) {
                UserAddressDTO addressDTO = userDTO.getAddresses();

                // Use ModelMapper to convert DTO to Entity
                UserAddress address = modelMapper.map(addressDTO, UserAddress.class);

                // Geocode address if needed
                if (addressDTO.getAddressDetails() != null && !addressDTO.getAddressDetails().isEmpty()) {
                    CarmenFeature geocodedFeature = mapBoxService.geocodeFeature(addressDTO.getAddressDetails());
                    if (geocodedFeature != null) {
                        address.setLatitude(geocodedFeature.center().latitude());
                        address.setLongitude(geocodedFeature.center().longitude());
                    }
                }

                newUser.setAddresses(address);
            }

            userRepository.save((User) user);
            // Send email verification
            emailServiceImpl.sendVerificationEmail(userDTO);
            ResponseEntity.status(HttpStatus.CREATED).body("User created successfully");
        } catch (Exception e) {
            LOGGER.error("Exception occurred while creating user: {}", e.getMessage(), e);
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create user");
        }

    }

    private boolean isPasswordAvailable(String newPassword) {
        // Check if the new password is already in the password history
        // Encode the new password and compare it with the password history
        // if it's OK then return true, if not return false.
        String hashedPassword = passwordEncoder.encode(newPassword);
        for (User user : userRepository.findAll()) {
            if (user.getPasswordHistory().contains(hashedPassword)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void updateUser(UserDetails user) {
        // try {
        //     // TODO: Handle update logic here, currently only handle the userName and
        //     // password fields,
        //     // after the tested, I should handle other fields in the future.
        //     // For now, just check if the user is an instance of User and check if username,
        //     // password exists.
        //     if (!(user instanceof User)) {
        //         throw new IllegalArgumentException("User must be an instance of User");
        //     }

        //     User updatedUser = (User) user;

        //     // Fetch the existing user from the repository
        //     User existingUser = userRepository.findByUserName(updatedUser.getUsername())
        //             .orElseThrow(() -> new IllegalArgumentException(
        //                     "User with username " + updatedUser.getUsername() + " not found"));

        //     // Update fields from the UserDetails
        //     existingUser.setUserName(updatedUser.getUsername());
        //     if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
        //         existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        //     }

        //     // TODO: It should be handled email and roles assignment right here
        //     // and the phoneNo and address should be handled in the future.

            

        //     // Save the updated user
        //     userRepository.save(existingUser);
        //     ResponseEntity.status(HttpStatus.OK).body("User updated successfully");
        // } catch (Exception e) {
        //     LOGGER.error("Exception occurred while updating user: {}", e.getMessage(), e);
        //     ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update user");
        // }

    }

    @Override
    public void deleteUser(String username) {
        // Hmmmm... Not sure if this is the right thing to do.
        // Only delete if the username is exists or not !?.
        // This should be handled more... I think.
        if (!userExists(username)) {
            throw new IllegalArgumentException("User with username " + username + " not found");
        } else {
            userRepository.deleteByUserName(username);
        }

    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                throw new IllegalStateException("User is not authenticated");
            }

            String userName = authentication.getName();

            if (!passwordEncoder.matches(oldPassword, userRepository.findByUserName(userName)
                    .orElseThrow(() -> new IllegalStateException("User not found")).getPassword())) {
                throw new IllegalArgumentException("Old password is not correct");
            }

            if (!PasswordValidation.isValidPassword(newPassword)) {
                throw new IllegalArgumentException("New password is not meet the security requirements");
            }

            userRepository.updatePassword(userName, passwordEncoder.encode(newPassword));
            ResponseEntity.status(HttpStatus.OK).body("Password updated successfully");
        } catch (Exception e) {
            LOGGER.error("Exception occurred while updating password: {}", e.getMessage(), e);
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update password");
        }

    }

}
