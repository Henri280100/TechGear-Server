package com.v01.techgear_server.constant;

public class ValidationMessageConstants {

    public static final String USERNAME_NOT_BLANK = "Username must not be blank";
    public static final String EMAIL_NOT_BLANK = "Email must not be blank";
    public static final String EMAIL_INVALID = "Email is not valid";
    public static final String PASSWORD_NOT_BLANK = "Password must not be blank";
    public static final String USER_ID_POSITIVE = "User ID must be a positive number";

    // Prevent instantiation
    private ValidationMessageConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}