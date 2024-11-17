package com.v01.techgear_server.service;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.v01.techgear_server.enums.Roles;
import com.v01.techgear_server.enums.UserPermission;
import com.v01.techgear_server.model.Role;
import com.v01.techgear_server.model.User;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

@Service
public class UserSpecifications {

    // Specification to filter users by roles
    /**
     * Specification to filter users by roles
     * @param roles the roles to filter by
     * @return a Specification that can be used to filter users by roles
     */
    public static Specification<User> hasRoles(Set<Roles> roles) {
        return (root, query, criteriaBuilder) -> {
            if (roles == null || roles.isEmpty()) {
                return criteriaBuilder.conjunction(); // Always match
            }

            // Join with roles and check role types
            Predicate rolesPredicate = root.join("roles")
                    .get("roleType")
                    .in(roles);

            return rolesPredicate;
        };
    }

    /**
     * Specification to filter users by specific permissions
     * @param permissions the permissions to filter by
     * @return a Specification that can be used to filter users by permissions
     */
    public static Specification<User> hasPermissions(Set<UserPermission> permissions) {
        return (root, query, criteriaBuilder) -> {
            if (permissions == null || permissions.isEmpty()) {
                return criteriaBuilder.conjunction(); // Always match
            }

            // Subquery to check if any role of the user has the required permissions
            Subquery<Long> subquery = query.subquery(Long.class);
            Root<User> subRoot = subquery.from(User.class);
            Join<User, Role> roleJoin = subRoot.join("roles");

            subquery.select(subRoot.get("id"))
                    .where(
                            roleJoin.get("roleType").get("permissions").in(permissions));

            return criteriaBuilder.exists(subquery);
        };
    }

    /**
     * Combine role and permission specifications
     * @param roles the roles to filter by
     * @param permissions the permissions to filter by
     * @return a Specification that can be used to filter users by roles or permissions
     */
    public static Specification<User> filterUsersWithRolesOrPermissions(
            Set<Roles> roles,
            Set<UserPermission> permissions) {

        return Specification.where(
                Optional.ofNullable(roles)
                        .map(UserSpecifications::hasRoles)
                        .orElse(null))
                .or(
                        Optional.ofNullable(permissions)
                                .map(UserSpecifications::hasPermissions)
                                .orElse(null));
    }

    /**
     * Specification for searching users by username or email
     * @param searchQuery the search query to filter by
     * @return a Specification that can be used to search users by username or email
     */
    public static Specification<User> searchByUsernameOrEmail(String searchQuery) {
        return (root, query, criteriaBuilder) -> {
            if (!StringUtils.hasText(searchQuery)) {
                return criteriaBuilder.conjunction();
            }

            String likePattern = "%" + searchQuery.toLowerCase() + "%";
            return criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("username")), likePattern),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), likePattern));
        };
    }

    /**
     * Comprehensive specification builder
     * @param searchQuery the search query to filter by
     * @param roles the roles to filter by
     * @param permissions the permissions to filter by
     * @return a Specification that can be used to filter users by search query, roles and permissions
     */
    public static Specification<User> buildUserSpecification(
            String searchQuery,
            Set<Roles> roles,
            Set<UserPermission> permissions) {

        Specification<User> spec = Specification.where(null);

        // Add search specification if search query is provided
        if (StringUtils.hasText(searchQuery)) {
            spec = spec.and(searchByUsernameOrEmail(searchQuery));
        }

        // Add roles specification if roles are provided
        if (roles != null && !roles.isEmpty()) {
            spec = spec.and(hasRoles(roles));
        }

        // Add permissions specification if permissions are provided
        if (permissions != null && !permissions.isEmpty()) {
            spec = spec.and(hasPermissions(permissions));
        }

        return spec;
    }
}
