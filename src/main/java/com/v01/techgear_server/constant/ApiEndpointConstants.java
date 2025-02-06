package com.v01.techgear_server.constant;

public class ApiEndpointConstants {
    public static final String USER_API = "/api/users";
    public static final String ROLE_API = "/api/roles";
    public static final String ACCOUNT_DETAILS_API = "/api/account-details";

    // Prevent instantiation
    private ApiEndpointConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
