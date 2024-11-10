package com.v01.techgear_server.security;

import java.io.Serializable;

import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CustomPermissionEvaluator implements PermissionEvaluator {

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        log.info("hasPermission(Object) called - Target: {}, Permission: {}", targetDomainObject, permission);
        if (authentication == null || !(permission instanceof String)) {
            return false;
        }

        // Handle null targetDomainObject case
        String targetType = targetDomainObject != null
                ? targetDomainObject.getClass().getSimpleName().toUpperCase()
                : "USER"; // Default to USER or appropriate default type

        return hasPrivilege(authentication, targetType, permission.toString().toUpperCase());
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType,
            Object permission) {
        log.info("hasPermission(TargetId) called - TargetId: {}, TargetType: {}, Permission: {}",
                targetId, targetType, permission);

        if (authentication == null || !(permission instanceof String)) {
            return false;
        }

        // Handle null targetType case
        String type = targetType != null ? targetType.toUpperCase() : "USER";

        return hasPrivilege(authentication, type, permission.toString().toUpperCase());
    }

    private boolean hasPrivilege(Authentication authentication, String targetType, String permission) {
        try {
            // Log the actual authorities for debugging
            log.debug("Checking privileges - User authorities: {}", authentication.getAuthorities());

            for (GrantedAuthority grantedAuthority : authentication.getAuthorities()) {
                String authority = grantedAuthority.getAuthority();
                log.debug("Checking authority: {} against target: {} and permission: {}",
                        authority, targetType, permission);

                // More flexible matching logic
                if (matchesPermission(authority, targetType, permission)) {
                    return true;
                }
            }
        } catch (Exception e) {
            log.error("Error checking privileges: ", e);
            return false;
        }
        return false;
    }

    private boolean matchesPermission(String authority, String targetType, String permission) {
        // Handle different permission formats
        // Example formats: "USER_READ", "ROLE_USER_READ", "USER:READ"
        String normalizedAuthority = authority.toUpperCase()
                .replace("ROLE_", "")
                .replace(":", "_");

        String expectedPermission = targetType + "_" + permission;

        // Log the comparison for debugging
        log.debug("Comparing normalized authority: {} with expected permission: {}",
                normalizedAuthority, expectedPermission);

        return normalizedAuthority.equals(expectedPermission) ||
                normalizedAuthority.endsWith(expectedPermission) ||
                // Add more matching patterns if needed
                authority.contains(permission);
    }
}
