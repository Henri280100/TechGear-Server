package com.v01.techgear_server.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.v01.techgear_server.enums.Roles;
import com.v01.techgear_server.model.Role;
import com.v01.techgear_server.repo.RoleRepository;

@Component
public class RoleInitializer {

    // @Autowired
    // private RoleRepository roleRepository;

    // @Override
    // public void run(String... args) {
    //     if (roleRepository.count() == 0) {
    //         Role adminRole = new Role();
    //         adminRole.setRoleType(Roles.ADMIN);
    //         roleRepository.save(adminRole);

    //         Role userRole = new Role();
    //         userRole.setRoleType(Roles.USER);
    //         roleRepository.save(userRole);
    //     }
    // }
}

