package com.v01.techgear_server.repo.jpa;

import com.v01.techgear_server.enums.UserPermission;
import com.v01.techgear_server.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserPermissionRepository extends JpaRepository<Permission, Long> {
	Optional<Permission> findByPermission(UserPermission userPermission);
}
