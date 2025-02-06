package com.v01.techgear_server.constant;

public class ErrorMessageConstants {
    // Error Messages
    public static final String USER_NOT_FOUND = "User not found";
    public static final String USER_ALREADY_EXISTS = "User already exists";
    public static final String USER_NOT_FOUND_WITH_ID = "User not found with ID: ";
    public static final String INVALID_PHONE_NUMBER = "Invalid phone number";
    public static final String ACCOUNT_DETAILS_NOT_FOUND = "Account details not found";
    public static final String ROLE_NOT_FOUND = "Role not found: ";
    public static final String USER_PHONE_NUMBER_NOT_FOUND = "User phone number not found";
    public static final String NO_SPECIFIED_ROLE = "User does not has specified role: ";
    public static final String USER_ID_NOT_FOUND = "User ID not found: ";
    public static final String USER_CANNOT_BE_NULL = "Username cannot be null";
    public static final String ACCOUNT_DETAILS_MISSING = "Account details are missing";
    public static final String USER_ADDRESS_NOT_FOUND = "User address not found";
    public static final String USER_ADDRESS_ID_NOT_FOUND = "User address ID not found: ";
    public static final String NOT_FOUND = "Not found";
    public static final String ERROR_DELETING_MEDIA = "Error deleting media from Cloudinary";
    public static final String ERROR_DELETING_MULTI_IMAGES = "Error deleting multiple images from Cloudinary";
    public static final String ERROR_DELETING_MULTI_MEDIA = "Error deleting multiple media from Cloudinary";
    public static final String ERROR_DELETING_IMAGE = "Error deleting images from Cloudinary";
    // Prevent instantiation
    private ErrorMessageConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
