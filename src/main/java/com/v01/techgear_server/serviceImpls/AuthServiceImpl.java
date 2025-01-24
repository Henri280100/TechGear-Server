package com.v01.techgear_server.serviceimpls;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

import com.v01.techgear_server.enums.Roles;
import com.v01.techgear_server.exception.UserAlreadyExistsException;
import com.v01.techgear_server.model.PasswordResetToken;
import com.v01.techgear_server.model.Role;
import com.v01.techgear_server.model.User;
import com.v01.techgear_server.repo.jpa.PasswordResetTokenRepository;
import com.v01.techgear_server.repo.jpa.RoleRepository;
import com.v01.techgear_server.repo.jpa.UserRepository;
import com.v01.techgear_server.utils.PasswordValidation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements UserDetailsManager {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;

    private static Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

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
            User users = (User) user;
            validateNewUser(users);

            users.setPassword(PasswordValidation.encodePassword(user.getPassword()));
            users.setUsername(user.getUsername());

            setUserRoles(users);

            userRepository.save(users);

        } catch (IllegalArgumentException | UserAlreadyExistsException e) {
            // Log and handle the specific argument errors
            logger.error("User creation error: {}", e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Preserve interrupt status
            logger.error("Unexpected error during user creation: ", e);
        } catch (ExecutionException e) {
            logger.error("Execution error: ", e);
        }
    }

    private void setUserRoles(User user) {
        Set<Role> roles = assignRoles(user);
        user.setRoles(roles);
    }

    private void validateNewUser(User user) throws InterruptedException, ExecutionException {
        CompletableFuture<Boolean> usernameCheck = CompletableFuture
                .supplyAsync(() -> isUsernameAvailable(user.getUsername()));
        CompletableFuture<Boolean> emailCheck = CompletableFuture
                .supplyAsync(() -> isUserEmailAvailable(user.getEmail()));
        CompletableFuture<Boolean> passwordCheck = CompletableFuture
                .supplyAsync(() -> isPasswordAvailable(user.getPassword()));

        // Wait for all checks to complete
        CompletableFuture<Void> allChecks = CompletableFuture.allOf(usernameCheck, emailCheck, passwordCheck);

        allChecks.join(); // Wait until all checks are done

        if (Boolean.FALSE.equals(usernameCheck.get())) {
            logger.error("Username already exists");
            throw new UserAlreadyExistsException("Username already exists");
        }
        if (Boolean.FALSE.equals(emailCheck.get())) {
            logger.error("Email {} already exists", user.getEmail());
            throw new UserAlreadyExistsException("Email already taken");
        }
        if (!PasswordValidation.isValidPassword(user.getPassword())) {
            logger.error("Password does not meet security requirements");
            throw new IllegalArgumentException("Password does not meet security requirements");
        }
        if (isPasswordAvailable(user.getPassword())) {
            logger.error("Password has been used before");
            throw new IllegalArgumentException("Password has been used before");
        }
    }

    private Set<Role> assignRoles(User user) {
        Set<Role> roles = new HashSet<>();

        // If no roles provided, assign the default USER role
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            // Assign the default USER role if no roles provided
            Role defaultRole = roleRepository.findByRoleType(Roles.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Default USER role not found."));
            roles.add(defaultRole);
        } else {
            // Iterate through provided roles and fetch from the database
            user.getRoles().forEach(providedRole -> {
                Role role = roleRepository.findByRoleType(providedRole.getRoleType())
                        .orElseThrow(() -> new RuntimeException("Error: Role not found."));
                roles.add(role);
            });
        }

        return roles;
    }

    private boolean isUserEmailAvailable(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        return user.isEmpty();
    }

    private boolean isUsernameAvailable(String username) {
        // Check if the username already exists in the database
        Optional<User> user = userRepository.findByUsername(username);
        return user.isEmpty();
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
        if (!userExists(user.getUsername())) {
            throw new IllegalArgumentException("User with username " + user.getUsername() + " not found");
        }

        User existingUser = userRepository.findByUsername(user.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        existingUser.setUsername(user.getUsername());
        existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        existingUser.setRoles(assignRoles((User) user));

        userRepository.save(existingUser);

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
            logger.error("Exception occurred while updating password: {}", e.getMessage(), e);
        }

    }

    public void onPasswordResetToken(User user, String token) {
        PasswordResetToken passwordResetToken = new PasswordResetToken();
        passwordResetToken.setToken(token);
        passwordResetToken.setUser(user);
        passwordResetToken.setExpiredDate(LocalDateTime.now().plusHours(24)); // Token valid for 24 hours
        passwordResetTokenRepository.save(passwordResetToken);
    }

    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User's email not found"));
    }

    public PasswordResetToken validatePasswordResetToken(String token) {
        return passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid token"));
    }

    public void updateUserPassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

}
