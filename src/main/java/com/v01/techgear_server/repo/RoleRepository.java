package com.v01.techgear_server.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.v01.techgear_server.model.Role;
import java.util.Set;
import com.v01.techgear_server.enums.Roles;


public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByRoleType(Roles roleType);
}
