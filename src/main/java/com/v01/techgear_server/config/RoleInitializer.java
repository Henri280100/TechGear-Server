package com.v01.techgear_server.config;

import com.v01.techgear_server.enums.UserPermission;
import com.v01.techgear_server.user.model.Permission;
import com.v01.techgear_server.user.repository.UserPermissionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.v01.techgear_server.enums.Roles;
import com.v01.techgear_server.user.model.Role;
import com.v01.techgear_server.user.repository.RoleRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
@Transactional
public class RoleInitializer implements CommandLineRunner {
	private final RoleRepository roleRepository;
	private final UserPermissionRepository permissionRepository;

	@Autowired
	public RoleInitializer(
			RoleRepository roleRepository,
			UserPermissionRepository permissionRepository
	) {
		this.roleRepository = roleRepository;
		this.permissionRepository = permissionRepository;
	}

	@Override
	public void run(String... args) {
		try {
			log.info("Checking if roles and permissions are already initialized");

			// Check if roles are already initialized
			if (roleRepository.count() == 0) {
				log.info("Initializing roles");
				Set<Role> roles = new HashSet<>();
				for (Roles role : Roles.values()) {
					Role newRole = new Role();
					newRole.setRoleType(role);
					roles.add(newRole);
				}
				roleRepository.saveAll(roles);

				// Initialize permissions
				log.info("Initializing permissions");
				Set<Permission> permissions = new HashSet<>();
				for (UserPermission permission : UserPermission.values()) {
					Permission newPermission = new Permission();
					newPermission.setPermission(permission);
					permissions.add(newPermission);
				}
				permissionRepository.saveAll(permissions);

				// Assign permissions to roles
				log.info("Assigning permissions to roles");
				roles.forEach(role -> {
					Set<Permission> rolePermissions = permissions.stream()
					                                             .filter(permission -> role.getRoleType()
					                                                                       .getPermissions()
					                                                                       .contains(
							                                                                       permission.getPermission()))
					                                             .collect(Collectors.toSet());
					role.setPermissions(rolePermissions);
					roleRepository.save(role);
				});
				log.info("Roles and permissions initialized");
			} else {
				log.info("Roles and permissions are already initialized");
			}

		} catch (Exception e) {
			log.error("Error initializing roles and permissions", e);
			throw new RuntimeException(e);
		}
	}

}
