package com.v01.techgear_server.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.v01.techgear_server.enums.Roles;
import com.v01.techgear_server.enums.UserPermission;
import com.v01.techgear_server.enums.UserStatus;
import com.v01.techgear_server.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    
    List<User> findAllByOrderByUsernameAsc();

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    // Find by username or email
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    // Find by username or email with custom query
    @Query("SELECT u FROM User u WHERE u.username = :identifier OR u.email = :identifier")
    Optional<User> findByUsernameOrEmail(@Param("identifier") String identifier);

    // Password-related queries
    @Modifying
    @Query("UPDATE User u SET u.password = :newPassword WHERE u.username = :username")
    void updatePassword(
            @Param("username") String username,
            @Param("newPassword") String newPassword);

    @Modifying
    @Query("UPDATE User u SET u.password = :newPassword WHERE u.id = :userId")
    void updatePasswordById(
            @Param("userId") Long userId,
            @Param("newPassword") String newPassword);

    // Soft delete or status update
    @Modifying
    @Query("UPDATE User u SET u.status = :status WHERE u.username = :username")
    void updateUserStatus(
            @Param("username") String username,
            @Param("status") UserStatus status);

    // Delete methods
    void deleteByUsername(String username);

    // Find users by status
    List<User> findByStatus(UserStatus status);

    // Find users by role
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.roleType = :roleType")
    List<User> findByRole(@Param("roleType") Roles roleType);

    // Find users with specific permissions
    @Query("SELECT DISTINCT u FROM User u " +
            "JOIN u.roles r " +
            "JOIN r.roleType rt " +
            "WHERE :permission MEMBER OF rt.permissions")
    List<User> findUsersWithPermission(@Param("permission") UserPermission permission);

    // Count methods
    long countByStatus(UserStatus status);

    long countByRolesRoleType(Roles roleType);

    // Complex search method
    @Query("SELECT u FROM User u " +
            "WHERE (:username IS NULL OR u.username LIKE %:username%) " +
            "AND (:email IS NULL OR u.email LIKE %:email%) " +
            "AND (:status IS NULL OR u.status = :status)")
    Page<User> searchUsers(
            @Param("username") String username,
            @Param("email") String email,
            @Param("status") UserStatus status,
            Pageable pageable);

    // Native query example for complex filtering
    @Query(value = "SELECT * FROM users u " +
            "JOIN user_roles ur ON u.id = ur.user_id " +
            "JOIN roles r ON ur.role_id = r.id " +
            "WHERE (:roleId IS NULL OR r.id = :roleId) " +
            "AND (:status IS NULL OR u.status = :status)", nativeQuery = true)
    List<User> findUsersByRoleAndStatus(
            @Param("roleId") Long roleId,
            @Param("status") String status);

    // Check if user has specific role
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END " +
            "FROM User u JOIN u.roles r WHERE u.id = :userId AND r.roleType = :roleType")
    boolean hasRole(
            @Param("userId") Long userId,
            @Param("roleType") Roles roleType);

}
