package com.v01.techgear_server.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.v01.techgear_server.enums.Roles;
import com.v01.techgear_server.model.Role;
import com.v01.techgear_server.repo.RoleRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RoleInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        if (roleRepository.count() == 0) {
            Role adminRole = new Role();
            if (roleRepository.findByRoleType(Roles.ROLE_ADMIN).isEmpty()) {

                adminRole.setRoleType(Roles.ROLE_ADMIN);
                roleRepository.save(adminRole);
            }
            if (roleRepository.findByRoleType(Roles.ROLE_USER).isEmpty()) {
                Role userRole = new Role();
                userRole.setRoleType(Roles.ROLE_USER);
                roleRepository.save(userRole);
            }
            if (roleRepository.findByRoleType(Roles.ROLE_MANAGER).isEmpty()) {
                Role userRole = new Role();
                userRole.setRoleType(Roles.ROLE_MANAGER);
                roleRepository.save(userRole);
            }
        }
    }
}
