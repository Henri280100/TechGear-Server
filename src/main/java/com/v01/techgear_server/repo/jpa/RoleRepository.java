package com.v01.techgear_server.repo.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.v01.techgear_server.model.Role;
import java.util.Optional;
import com.v01.techgear_server.enums.Roles;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByRoleType(Roles roleType);

	boolean existsByRoleType(Roles roleType);
}
