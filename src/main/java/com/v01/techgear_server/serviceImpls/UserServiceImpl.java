package com.v01.techgear_server.serviceimpls;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.v01.techgear_server.constant.ErrorMessageConstants;
import com.v01.techgear_server.dto.AccountDetailsDTO;
import com.v01.techgear_server.dto.ImageDTO;
import com.v01.techgear_server.dto.UserAddressDTO;
import com.v01.techgear_server.dto.UserDTO;
import com.v01.techgear_server.dto.UserPhoneNoDTO;
import com.v01.techgear_server.enums.CommunicationPreference;
import com.v01.techgear_server.enums.Roles;
import com.v01.techgear_server.enums.UserStatus;
import com.v01.techgear_server.exception.BadRequestException;
import com.v01.techgear_server.exception.UserAddressNotFoundException;
import com.v01.techgear_server.exception.UserNotFoundException;
import com.v01.techgear_server.mapping.AccountDetailsMapper;
import com.v01.techgear_server.mapping.ImageMapper;
import com.v01.techgear_server.mapping.UserAddressMapper;
import com.v01.techgear_server.mapping.UserMapper;
import com.v01.techgear_server.mapping.UserPhoneNoMapper;
import com.v01.techgear_server.model.AccountDetails;
import com.v01.techgear_server.model.Role;
import com.v01.techgear_server.model.User;
import com.v01.techgear_server.model.UserAddress;
import com.v01.techgear_server.model.UserPhoneNo;
import com.v01.techgear_server.repo.jpa.AccountDetailsRepository;
import com.v01.techgear_server.repo.jpa.RoleRepository;
import com.v01.techgear_server.repo.jpa.UserAddressRepository;
import com.v01.techgear_server.repo.jpa.UserPhoneNoRepository;
import com.v01.techgear_server.repo.jpa.UserRepository;
import com.v01.techgear_server.service.FileStorageService;
import com.v01.techgear_server.service.UserService;
import com.v01.techgear_server.utils.PhoneNumberValidator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final UserPhoneNoRepository userPhoneNoRepository;
	private final UserAddressRepository userAddressRepository;
	private final AccountDetailsRepository accountDetailsRepository;

	private final FileStorageService fileStorageService;

	private final UserDetailsManager userDetailsManager;
	private final AccountDetailsMapper accountDetailsMapper;
	private final UserPhoneNoMapper userPhoneNoMapper;
	private final UserMapper userMapper;
	private final UserAddressMapper userAddressMapper;
	private final ImageMapper imageMapper;

	@Override
	public CompletableFuture<UserPhoneNoDTO> saveUserPhoneNo(AccountDetailsDTO accountDetailsDTO) {
		return CompletableFuture.supplyAsync(() -> validateAndMapPhoneNo(accountDetailsDTO))
		                        .thenCompose(phoneNo -> {
			                        if (phoneNo.getId() == null) {
				                        // Save new phone number
				                        return CompletableFuture.supplyAsync(
						                        () -> saveNewPhoneNumber(accountDetailsDTO, phoneNo));
			                        } else {
				                        // Update existing phone number
				                        return CompletableFuture.supplyAsync(
						                        () -> updateExistingPhoneNumber(accountDetailsDTO, phoneNo));
			                        }
		                        })
		                        .thenApply(userPhoneNoMapper::toDTO);
	}

	private UserPhoneNo validateAndMapPhoneNo(AccountDetailsDTO accountDetailsDTO) {
		// Validate user and phone number existence
		if (accountDetailsDTO == null || accountDetailsDTO.getUserPhoneNoDTOS() == null
				|| accountDetailsDTO.getUserPhoneNoDTOS()
				                    .isEmpty()) {
			throw new IllegalArgumentException("User or phone number information is missing.");
		}

		// Initialize phone number validator
		PhoneNumberValidator phoneNumberValidator = new PhoneNumberValidator();
		UserPhoneNoDTO phoneNoDTO = accountDetailsDTO.getUserPhoneNoDTOS()
		                                             .stream()
		                                             .findFirst()
		                                             .orElseThrow(() -> new IllegalArgumentException(
				                                             "No phone numbers found in account details."));

		UserPhoneNo phoneNo = userPhoneNoMapper.toEntity(phoneNoDTO);
		if (phoneNo == null) {
			throw new IllegalArgumentException("Phone number entity is null.");
		}

		String countryCode = phoneNo.getCountryCode();
		if (!StringUtils.hasText(countryCode)) {
			throw new IllegalArgumentException("Country code is missing.");
		}

		if (phoneNo.isPrimary()) {
			// Validate primary phone number
			validatePrimaryPhoneNumber(accountDetailsDTO, phoneNo);
		}

		// Validate communication preference
		CommunicationPreference preference = phoneNo.getCommunicationPreference();
		if (preference == null) {
			throw new IllegalArgumentException("Communication preference is missing.");
		}

		try {
			phoneNo.setCommunicationPreference(CommunicationPreference.valueOf(preference.name()
			                                                                             .toUpperCase()));
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("Invalid communication preference: " + preference);
		}

		// Validate phone number
		validatePhoneNumber(phoneNo, countryCode, phoneNumberValidator);

		return phoneNo;
	}

	private void validatePhoneNumber(UserPhoneNo phoneNo, String countryCode, PhoneNumberValidator validator) {
		if (!validator.isValidPhoneNumber(phoneNo.getPhoneNo(), countryCode)) {
			throw new IllegalArgumentException(ErrorMessageConstants.INVALID_PHONE_NUMBER + phoneNo.getPhoneNo());
		}
		// Format the phone number if valid
		phoneNo.setPhoneNo(validator.formatPhoneNumber(phoneNo.getPhoneNo(), countryCode));
	}

	private UserPhoneNo saveNewPhoneNumber(AccountDetailsDTO accountDetailsDTO, UserPhoneNo phoneNo) {
		return saveOrUpdatePhoneNumber(accountDetailsDTO, phoneNo);
	}

	private UserPhoneNo updateExistingPhoneNumber(AccountDetailsDTO accountDetailsDTO, UserPhoneNo phoneNo) {
		return saveOrUpdatePhoneNumber(accountDetailsDTO, phoneNo);
	}

	private void validatePrimaryPhoneNumber(AccountDetailsDTO accountDetailsDTO, UserPhoneNo phoneNo) {
		for (UserPhoneNoDTO existingPhoneNoDTO : accountDetailsDTO.getUserPhoneNoDTOS()) {
			if (existingPhoneNoDTO.isPrimary() && !existingPhoneNoDTO.getId()
			                                                         .equals(phoneNo.getId())) {
				throw new IllegalArgumentException("Another primary phone number already exists.");
			}
		}
	}

	private UserPhoneNo saveOrUpdatePhoneNumber(AccountDetailsDTO accountDetailsDTO, UserPhoneNo phoneNo) {
		AccountDetails accountDetails = accountDetailsMapper.toEntity(accountDetailsDTO);
		phoneNo.setAccountDetails(accountDetails);
		return userPhoneNoRepository.save(phoneNo);
	}

	/// Adds a connection between two users.
	/// This method establishes a connection between the user identified by userId
	/// and the user identified by connectionUserId.
	/// The exact nature of this
	/// connection
	/// depends on the application's social or networking features.
	///
	/// @param userId           The ID of the user initiating the connection.
	/// @param connectionUserId The ID of the user to be connected with.
	///
	/// @return A CompletableFuture that, when completed, will contain the UserDTO
	///         of the user who initiated the connection, potentially updated with
	///         the new connection information.
	/// Returns null if the operation is
	///         not yet implemented.
	@Override
	public CompletableFuture<UserDTO> addUserConnection(Long userId, Long connectionUserId) {
		return CompletableFuture.supplyAsync(() -> {
			User user = userRepository.findById(userId)
			                          .orElseThrow(
					                          () -> new UserNotFoundException(
							                          ErrorMessageConstants.USER_NOT_FOUND_WITH_ID + userId));
			User connectionUser = userRepository.findById(connectionUserId)
			                                    .orElseThrow(() -> new UserNotFoundException(
					                                    ErrorMessageConstants.USER_NOT_FOUND_WITH_ID + connectionUserId));

			// Add the connection
			user.getConnections()
			    .add(connectionUser);
			userRepository.save(user);

			return userMapper.toDTO(user);
		});
	}

	@Override
	public CompletableFuture<UserDTO> assignRole(Long userId, String roleName) {
		// Validate input parameters
		validateInputParameters(userId, roleName);

		// Use executor service for async processing
		return CompletableFuture.supplyAsync(() -> {
			                        try {
				                        // Find User with Optional Handling
				                        User user = findUserById(userId);

				                        // Find and Validate Role
				                        Role role = findAndValidateRole(roleName);

				                        // Update User Roles
				                        updateUserRoles(user, role);

				                        // Save and Map User
				                        return saveAndMapUser(user);

			                        } catch (UserNotFoundException | IllegalArgumentException e) {
				                        // Log the error
				                        log.error("Role Assignment Error: {}", e.getMessage());

				                        // Propagate specific exceptions
				                        throw new CompletionException(e);
			                        }
		                        })
		                        .exceptionally(this::handleRoleAssignmentException);
	}

	// Input Validation
	private void validateInputParameters(Long userId, String roleName) {
		Objects.requireNonNull(userId, "User ID cannot be null");
		Objects.requireNonNull(roleName, "Role name cannot be null");

		if (userId <= 0) {
			throw new IllegalArgumentException("Invalid user ID");
		}

		if (roleName.trim()
		            .isEmpty()) {
			throw new IllegalArgumentException("Role name cannot be empty");
		}
	}

	// User Retrieval with Enhanced Error Handling
	private User findUserById(Long userId) {
		return userRepository.findById(userId)
		                     .orElseThrow(() -> new UserNotFoundException(
				                     "User not found with ID: %d".formatted(userId)));
	}

	// Role Retrieval and Validation
	private Role findAndValidateRole(String roleName) {
		try {
			Roles roleType = Roles.valueOf(roleName.toUpperCase());
			return roleRepository.findByRoleType(roleType)
			                     .orElseThrow(() -> new IllegalArgumentException(
					                     "Role not found: %s".formatted(roleName)));
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException(
					"Invalid role name: %s".formatted(roleName));
		}
	}

	// Update User Roles
	private void updateUserRoles(User user, Role role) {
		// Clear existing roles and add a new role
		user.setRoles(Set.of(role));
	}

	// Save and Map User
	private UserDTO saveAndMapUser(User user) {
		User savedUser = userRepository.save(user);
		return userMapper.toDTO(savedUser);
	}

	// Global Exception Handler
	private UserDTO handleRoleAssignmentException(Throwable ex) {
		if (ex instanceof UserNotFoundException) {
			log.error("User not found during role assignment", ex);
			throw new CompletionException(ex);
		}

		if (ex instanceof IllegalArgumentException) {
			log.warn("Invalid role assignment: {}", ex.getMessage());
			throw new CompletionException(ex);
		}

		log.error("Unexpected error during role assignment", ex);
		throw new CompletionException(new RuntimeException("Role assignment failed", ex));
	}


	@Override
	public CompletableFuture<List<UserDTO>> bulkCreateUsers(List<UserDTO> userDTO) {
		return CompletableFuture.supplyAsync(() -> {
			if (userDTO == null || userDTO.isEmpty()) {
				throw new IllegalArgumentException("UserDTO list cannot be null or empty");
			}

			List<User> users = new ArrayList<>();
			for (UserDTO dto : userDTO) {
				User user = userMapper.toEntity(dto);
				users.add(user);
			}

			List<User> savedUsers = userRepository.saveAll(users);
			return userMapper.toDTOList(savedUsers);
		});
	}

	@Override
	public CompletableFuture<Void> bulkDeleteUsers(List<Long> userIds) {
		return CompletableFuture.runAsync(() -> {
			if (userIds == null || userIds.isEmpty()) {
				throw new IllegalArgumentException("User IDs list cannot be null or empty");
			}

			List<User> users = new ArrayList<>();
			for (Long userId : userIds) {
				User user = userRepository.findById(userId)
				                          .orElseThrow(
						                          () -> new UserNotFoundException(
								                          ErrorMessageConstants.USER_NOT_FOUND_WITH_ID + userId));
				users.add(user);
			}

			userRepository.deleteAll(users);
		});
	}

	@Override
	public CompletableFuture<List<UserDTO>> bulkUpdateUsers(List<UserDTO> userDTO) {
		return CompletableFuture.supplyAsync(() -> {
			if (userDTO == null || userDTO.isEmpty()) {
				throw new IllegalArgumentException("UserDTO list cannot be null or empty");
			}

			List<User> users = new ArrayList<>();
			for (UserDTO dto : userDTO) {
				User user = userRepository.findById(dto.getUserId())
				                          .orElseThrow(() -> new UserNotFoundException(
						                          ErrorMessageConstants.USER_NOT_FOUND_WITH_ID + dto.getUserId()));
				userMapper.updateEntityFromDTO(dto, user);
				users.add(user);
			}

			List<User> updatedUsers = userRepository.saveAll(users);
			return userMapper.toDTOList(updatedUsers);
		});
	}

	/**
	 * Creates a new user address based on the provided account details.
	 *
	 * @param accountDetailsDTO the account details containing the user address
	 *                          information
	 *
	 * @return a CompletableFuture containing the created UserAddressDTO
	 *
	 * @throws IllegalArgumentException if the account details or user address
	 *                                  information is missing
	 */
	@Override
	public CompletableFuture<UserAddressDTO> createUserNewAddress(AccountDetailsDTO accountDetailsDTO) {
		return CompletableFuture.supplyAsync(() -> {
			if (accountDetailsDTO == null) {
				throw new IllegalArgumentException(ErrorMessageConstants.ACCOUNT_DETAILS_MISSING);
			}
			if (accountDetailsDTO.getUserAddressDTOS() == null) {
				throw new IllegalArgumentException(ErrorMessageConstants.USER_ADDRESS_NOT_FOUND);
			}

			// Get the first UserAddressDTO from the set
			Iterator<UserAddressDTO> iterator = accountDetailsDTO.getUserAddressDTOS()
			                                                     .iterator();
			if (!iterator.hasNext()) {
				throw new IllegalArgumentException(ErrorMessageConstants.USER_ADDRESS_NOT_FOUND);
			}
			UserAddressDTO userAddressDTO = iterator.next();

			AccountDetails accountDetails = accountDetailsMapper.toEntity(accountDetailsDTO);
			userAddressDTO.setAccountDetailsDTO(accountDetailsMapper.toDTO(accountDetails));

			// Map and save the user address in one step
			UserAddress userAddress = userAddressRepository.save(userAddressMapper.toEntity(userAddressDTO));

			return userAddressMapper.toDTO(userAddress);
		});
	}

	@Override
	public CompletableFuture<UserDTO> deactivateUser(Long userId) {
		return CompletableFuture.supplyAsync(() -> {
			User user = userRepository.findById(userId)
			                          .orElseThrow(() -> new UserNotFoundException(
					                          ErrorMessageConstants.USER_ID_NOT_FOUND + userId));
			user.setUserStatus(UserStatus.INACTIVE);
			return userMapper.toDTO(userRepository.save(user));
		});
	}

	@Override
	public CompletableFuture<List<UserDTO>> findInactiveUsers(int days) {
		return CompletableFuture.supplyAsync(() -> {
			// Get the inactive users
			// Assuming UserStatus enum is defined as INACTIVE
			// Replace UserStatus.INACTIVE with the actual enum value
			// Example: UserStatus.INACTIVE.name() -> "INACTIVE"
			// Example: userRepository.findByStatus(UserStatus.valueOf("INACTIVE")) ->
			// returns a List<User> of inactive users
			// Example: userMapper.toDTOList(inactiveUsers) -> maps the List<User> to
			// List<UserDTO> and returns the DTOs of inactive users
			if (days <= 0) {
				throw new IllegalArgumentException("Days must be a positive number");
			}

			LocalDateTime inactiveSince = LocalDateTime.now()
			                                           .minusDays(days);
			List<User> inactiveUsers = userRepository.findByUserStatusAndLastLoginActiveBefore(UserStatus.INACTIVE,
			                                                                          inactiveSince);

			if (inactiveUsers == null || inactiveUsers.isEmpty()) {
				return new ArrayList<>();
			}

			// Map the entities to DTOs
			return userMapper.toDTOList(inactiveUsers);
		});
	}

	@Override
	public CompletableFuture<List<UserDTO>> findPotentialConnections(Long userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<List<UserDTO>> findRecentlyRegisteredUsers(int days) {
		return CompletableFuture.supplyAsync(() -> {
			// Get the recently registered users
			LocalDateTime registeredSince = LocalDateTime.now()
			                                             .minusDays(days);
			List<User> recentlyRegisteredUsers = userRepository.findByCreatedDateAfter(registeredSince);

			// Map the entities to DTOs
			return userMapper.toDTOList(recentlyRegisteredUsers);
		});
	}

	@Override
	public CompletableFuture<List<UserDTO>> findUsersByAgeRange(int minAge, int maxAge) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<List<UserAddressDTO>> findUsersByCountry(String country) {
		return CompletableFuture.supplyAsync(() -> {
			// Validate country
			if (!StringUtils.hasText(country)) {
				throw new IllegalArgumentException("Country cannot be null or empty");
			}

			// Find users by country
			List<UserAddress> userAddresses = userAddressRepository.findByCountry(country);

			// Check if userAddresses list is empty
			if (userAddresses == null || userAddresses.isEmpty()) {
				return new ArrayList<>();
			}

			return userAddressMapper.toDTOList(userAddresses);
		});
	}

	@Override
	public CompletableFuture<List<UserDTO>> findUsersByRole(String roleName, String status) {
		return CompletableFuture.supplyAsync(() -> {
			// Validate role name and status
			if (roleName == null || roleName.isEmpty()) {
				throw new IllegalArgumentException("Role name cannot be null or empty");
			}
			if (status == null || status.isEmpty()) {
				throw new IllegalArgumentException("Status cannot be null or empty");
			}

			// Find the role by name
			Role role = roleRepository.findByRoleType(Roles.valueOf(roleName.toUpperCase()))
			                          .orElseThrow(() -> new IllegalArgumentException(
					                          ErrorMessageConstants.ROLE_NOT_FOUND + roleName));

			// Find users by role and status
			List<User> users = userRepository.findUserByRoleNameAndStatus(role.getRoleType()
			                                                                  .name(),
			                                                              UserStatus.valueOf(status.toUpperCase()));

			// Check if user's list is empty
			if (users == null || users.isEmpty()) {
				return new ArrayList<>();
			}

			// Map the entities to DTOs
			return userMapper.toDTOList(users);
		});
	}

	@Override
	public CompletableFuture<List<UserDTO>> getUserConnections(Long userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Boolean> hasRole(Long userId, String roleName) {
		return CompletableFuture.supplyAsync(() -> {
			// Validate user ID and role name
			if (userId == null || userId <= 0) {
				throw new IllegalArgumentException("User ID cannot be null or zero");
			}
			if (roleName == null || roleName.isEmpty()) {
				throw new IllegalArgumentException("Role name cannot be null or empty");
			}
			// Find the role by name
			Role role = roleRepository.findByRoleType(Roles.valueOf(roleName.toUpperCase()))
			                          .orElseThrow(() -> new IllegalArgumentException("Role not found: " + roleName));

			// Check if the user has the specified role
			User user = userRepository.findById(userId)
			                          .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

			return user.getRoles()
			           .contains(role);
		});
	}

	@Override
	public CompletableFuture<Boolean> isUserLocked(Long userId) {
		return CompletableFuture.supplyAsync(() -> {
			// Validate user ID
			if (userId == null || userId <= 0) {
				throw new IllegalArgumentException("User ID cannot be null or zero");
			}

			// Find the user by ID
			User user = userRepository.findById(userId)
			                          .orElseThrow(() -> new UserNotFoundException(
					                          ErrorMessageConstants.USER_ID_NOT_FOUND + userId));

			// Check if the user is locked
			return !user.isAccountNonLocked();
		});
	}

	@Override
	public CompletableFuture<UserDTO> lockUser(Long userId) {
		return CompletableFuture.supplyAsync(() -> {
			User user = userRepository.findById(userId)
			                          .orElseThrow(() -> new UserNotFoundException(
					                          ErrorMessageConstants.USER_ID_NOT_FOUND + userId));
			user.setAccountLocked(true);
			return userMapper.toDTO(userRepository.save(user));
		});
	}

	@Override
	public CompletableFuture<UserDTO> reactivateUser(Long userId) {
		return CompletableFuture.supplyAsync(() -> {
			User user = userRepository.findById(userId)
			                          .orElseThrow(() -> new UserNotFoundException(
					                          ErrorMessageConstants.USER_ID_NOT_FOUND + userId));
			user.setUserStatus(UserStatus.ACTIVE);
			return userMapper.toDTO(userRepository.save(user));
		});
	}

	@Override
	public CompletableFuture<UserDTO> removeRole(Long userId, String roleName) {
		return CompletableFuture.supplyAsync(() -> {
			User user = userRepository.findById(userId)
			                          .orElseThrow(() -> new UserNotFoundException(
					                          ErrorMessageConstants.USER_ID_NOT_FOUND + userId));
			Role role = roleRepository.findByRoleType(Roles.valueOf(roleName.toUpperCase()))
			                          .orElseThrow(() -> new IllegalArgumentException(
					                          ErrorMessageConstants.ROLE_NOT_FOUND + roleName));

			if (!user.getRoles()
			         .contains(role)) {
				throw new IllegalArgumentException(ErrorMessageConstants.NO_SPECIFIED_ROLE + roleName);
			}

			user.getRoles()
			    .remove(role);
			return userMapper.toDTO(userRepository.save(user));
		});
	}

	@Override
	public CompletableFuture<Void> removeUserConnection(Long userId, Long connectionUserId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<UserDTO> unlockUser(Long userId) {
		return CompletableFuture.supplyAsync(() -> {
			User user = userRepository.findById(userId)
			                          .orElseThrow(() -> new UserNotFoundException(
					                          ErrorMessageConstants.USER_ID_NOT_FOUND + userId));
			user.setAccountLocked(false);
			return userMapper.toDTO(userRepository.save(user));
		});
	}

	@Override
	public CompletableFuture<UserAddressDTO> updateUserAddress(Long userAddressId, UserAddressDTO userAddressDTO) {
		return CompletableFuture.supplyAsync(() -> {
			                        if (userAddressDTO == null || userAddressDTO.getId() == null) {
				                        throw new IllegalArgumentException("UserAddressDTO or User Address ID cannot be null");
			                        }
			                        return userAddressRepository.findById(userAddressId)
			                                                    .orElseThrow(
					                                                    () -> new UserAddressNotFoundException(
							                                                    ErrorMessageConstants.USER_ADDRESS_ID_NOT_FOUND + userAddressId));

		                        })
		                        .thenCompose(existingUserAddress -> {
			                        userAddressMapper.updateEntityFromDTO(userAddressDTO, existingUserAddress);
			                        return CompletableFuture.supplyAsync(
					                        () -> userAddressRepository.save(existingUserAddress));
		                        })
		                        .thenApply(userAddressMapper::toDTO);
	}

	@Override
	public CompletableFuture<UserDTO> updateUsername(Long userId, String username) {
		return CompletableFuture.supplyAsync(() -> {
			                        if (userId == null) {
				                        throw new IllegalArgumentException("User ID or UserDTO cannot be null");
			                        }
			                        if (username == null) {
				                        throw new IllegalArgumentException(ErrorMessageConstants.USER_CANNOT_BE_NULL);
			                        }
			                        return userRepository.findById(userId)
			                                             .orElseThrow(() -> new UserNotFoundException(
					                                             ErrorMessageConstants.USER_ID_NOT_FOUND + userId));

		                        })
		                        .thenCompose(existingUserName -> {
			                        existingUserName.setUsername(username);
			                        return CompletableFuture.supplyAsync(() -> userRepository.save(existingUserName));
		                        })
		                        .thenApply(userMapper::toDTO);
	}

	@Override
	public CompletableFuture<UserDTO> updateUserEmail(Long userId, String email) {
		return CompletableFuture.supplyAsync(() -> {
			User user = userRepository.findById(userId)
			                          .orElseThrow(
					                          () -> new UserNotFoundException("User not found with email: " + email));
			user.setEmail(email);
			return userMapper.toDTO(userRepository.save(user));
		});
	}

	@Override
	public CompletableFuture<UserDTO> getUserById(Long userId) {
		try {
			return CompletableFuture.supplyAsync(() -> userRepository.findById(userId)
			                                                         .map(userMapper::toDTO)
			                                                         .orElseThrow(() -> new UserNotFoundException(
					                                                         "User with ID " + userId + ErrorMessageConstants.NOT_FOUND)));
		} catch (UserNotFoundException e) {
			log.error("Error retrieving user with ID {}: {}", userId, e.getMessage());
			throw e;
		}
	}

	@Override
	public CompletableFuture<UserDTO> findUserByUsernameOrEmail(String username, String email) {
		return CompletableFuture.supplyAsync(() -> {
			// Validate inputs
			validateInputs(username, email);

			// Determine the identifier to use
			String identifier = determineIdentifier(username, email);

			// Use the repository method to find the user
			User user = userRepository.findByUsernameOrEmail(identifier)
			                          .orElseThrow(() -> new UserNotFoundException(
					                          "User not found with username: " + username + " or email: " + email));
			return userMapper.toDTO(user);
		});
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
	public CompletableFuture<UserDTO> deleteUserById(Long userId) {
		return CompletableFuture.supplyAsync(() -> {
			                        User user = userRepository.findById(userId)
			                                                  .orElseThrow(
					                                                  () -> new UserNotFoundException("User  not found with id " + userId));

			                        userRepository.deleteById(userId);

			                        return userMapper.toDTO(user);
		                        })
		                        .exceptionally(throwable -> {
			                        log.error("Error deleting user with ID {}: {}", userId, throwable.getMessage());
			                        throw new UserNotFoundException("User with ID " + userId + " not found", throwable);
		                        });
	}

	@Override
	public CompletableFuture<UserDTO> deleteByUsername(Long userId, String username) {

		// Use UserDetailsManager to load the user
		UserDetails userDetails = userDetailsManager.loadUserByUsername(username);
		Optional<User> userOptional = userRepository.findByUsername(username);
		if (userOptional.isPresent()) {
			User user = userOptional.get();
			if (!Objects.equals(user.getUserId(), userId)) {
				throw new BadRequestException("User ID does not match the user to be deleted");
			}
		} else {
			throw new UserNotFoundException("User not found with username " + username);
		}
		// Check if the provided userId matches the loaded user
		if (!userDetails.getUsername()
		                .equals(username)
				|| !userId.equals(userOptional.get()
				                              .getUserId())) {
			throw new BadRequestException("Username or User ID does not match the user to be deleted");
		}

		// Delete the user using UserDetailsManager
		userDetailsManager.deleteUser(username);

		// You might need to fetch and return the User entity from the repository
		// if you need additional information from the entity
		return CompletableFuture.supplyAsync(() -> {
			User user = userRepository.findByUsername(username)
			                          .orElseThrow(() -> new UserNotFoundException(
					                          "User not found with username " + username));
			return userMapper.toDTO(user);
		});

	}

	@Override
	public CompletableFuture<ImageDTO> uploadUserAvatar(MultipartFile userAvatar, AccountDetailsDTO accountDetailsDTO) {
		validateAvatarFile(userAvatar);

		return CompletableFuture.supplyAsync(
				                        () -> accountDetailsRepository.findById(accountDetailsDTO.getAccountDetailsId())
				                                                      .orElseThrow(
						                                                      () -> new UserNotFoundException("User profile with ID "
								                                                                                      + accountDetailsDTO.getAccountDetailsId() + " not found")))
		                        .thenCompose(accountDetails -> {
			                        // Now we can upload the image asynchronously
			                        try {
				                        return fileStorageService.storeSingleImage(userAvatar)
				                                                 .thenApply(imageDTO -> {
					                                                 // Set the user avatar and save the user
					                                                 accountDetails.setUserAvatar(
							                                                 imageMapper.toEntity(imageDTO));
					                                                 accountDetailsRepository.save(accountDetails);
					                                                 return imageDTO; // Return the ImageDTO
				                                                 });
			                        } catch (IOException e) {
				                        throw new BadRequestException("Failed to upload user avatar. Please try again.",
				                                                      e.getCause());
			                        }
		                        });
	}

	@Override
	public CompletableFuture<ImageDTO> updateUserAvatar(Long accountDetailsId, MultipartFile newUserAvatar,
	                                                    AccountDetailsDTO accountDetailsDTO) {
		// Validate the userAvatar file
		validateAvatarFile(newUserAvatar);
		return CompletableFuture.supplyAsync(() -> accountDetailsRepository.findById(accountDetailsId)
		                                                                   .orElseThrow(
				                                                                   () -> new UserNotFoundException(
						                                                                   "USer profile with ID " + accountDetailsId + " not found.")))
		                        .thenCompose(accountDetails -> {
			                        try {
				                        // Check if the current avatar exists and delete it
				                        if (accountDetails.getUserAvatar() != null) {
					                        fileStorageService.deleteImage(accountDetails.getUserAvatar()
					                                                                     .getId()
					                                                                     .toString());
				                        }

				                        // Store the new avatar
				                        return fileStorageService.storeSingleImage(newUserAvatar)
				                                                 .thenApply(imageDTO -> {
					                                                 // Set the user avatar and save the user
					                                                 accountDetails.setUserAvatar(
							                                                 imageMapper.toEntity(imageDTO));
					                                                 accountDetailsRepository.save(accountDetails);
					                                                 return imageDTO; // Return the ImageDTO
				                                                 });
			                        } catch (IOException e) {
				                        throw new BadRequestException("Failed to update user avatar. Please try again.",
				                                                      e.getCause());
			                        }
		                        });
	}

	/**
	 * Helper method to validate the avatar file.
	 *
	 * @param userAvatar the file to be validated
	 *
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
	 *
	 * @return true if valid, false otherwise
	 */
	private boolean isValidImageType(MultipartFile userAvatar) {
		String contentType = userAvatar.getContentType();
		return contentType != null &&
				(contentType.equals("image/png") || contentType.equals("image/jpeg")
						|| contentType.equals("image/jpg"));
	}

}