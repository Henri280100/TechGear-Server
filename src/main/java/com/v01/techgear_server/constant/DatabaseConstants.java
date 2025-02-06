package com.v01.techgear_server.constant;

public class DatabaseConstants {
    public static final String USER_TABLE = "users";
    public static final String ROLE_TABLE = "roles";
    public static final String ACCOUNT_DETAILS_TABLE = "account_details";

    // Prevent instantiation
    private DatabaseConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
