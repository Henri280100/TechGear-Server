package com.v01.techgear_server.enums;

import java.util.Set;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public enum Roles {
        ROLE_USER(Set.of(
                        UserPermission.PRODUCT_READ,
                        UserPermission.ORDER_READ_OWN,
                        UserPermission.PROFILE_READ_OWN,
                        UserPermission.PROFILE_UPDATE_OWN,
                        UserPermission.REVIEW_CREATE,
                        UserPermission.REVIEW_READ_OWN)),

        ROLE_ADMIN(Set.of(
                        UserPermission.PRODUCT_READ,
                        UserPermission.PRODUCT_CREATE,
                        UserPermission.PRODUCT_UPDATE,
                        UserPermission.PRODUCT_DELETE,
                        UserPermission.ORDER_READ,
                        UserPermission.ORDER_CREATE,
                        UserPermission.ORDER_UPDATE,
                        UserPermission.ORDER_PROCESS,
                        UserPermission.USER_READ,
                        UserPermission.USER_CREATE,
                        UserPermission.USER_UPDATE,
                        UserPermission.USER_DELETE,
                        UserPermission.ACCESS_ADMIN_PANEL,
                        UserPermission.DISCOUNT_MANAGE,
                        UserPermission.INVENTORY_MANAGE,
                        UserPermission.REPORT_GENERATE)),

        ROLE_MANAGER(Set.of(
                        UserPermission.PRODUCT_READ,
                        UserPermission.PRODUCT_CREATE,
                        UserPermission.PRODUCT_UPDATE,
                        UserPermission.ORDER_READ,
                        UserPermission.ORDER_UPDATE,
                        UserPermission.ORDER_PROCESS,
                        UserPermission.USER_READ,
                        UserPermission.REPORT_VIEW_ALL,
                        UserPermission.CUSTOMER_SUPPORT_ACCESS,
                        UserPermission.CUSTOMER_SUPPORT_MANAGE,
                        UserPermission.STAFF_MANAGE));

        private final Set<UserPermission> permissions;

	Roles(Set<UserPermission> permissions) {
		this.permissions = permissions;
	}
}
