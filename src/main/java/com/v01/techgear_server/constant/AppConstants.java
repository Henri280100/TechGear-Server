package com.v01.techgear_server.constant;

public class AppConstants {
    public static final String DEFAULT_COUNTRY_CODE = "+1";
    public static final int MAX_LOGIN_ATTEMPTS = 10;

    // Prevent instantiation
    private AppConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
