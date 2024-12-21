package com.v01.techgear_server.enums;

public class ApiResponseStatus {
        private static final String STATUS_FAILURE = "failure";
        private static final String STATUS_SUCCESS = "success";

        private final String status;

        private final String message;

        public ApiResponseStatus(String status, String message) {

                this.status = status;

                this.message = message;

        }

        public String getStatus() {

                return status;

        }

        public String getMessage() {

                return message;

        }

        public static final ApiResponseStatus SUCCESS = new ApiResponseStatus(STATUS_SUCCESS, "Operation successful");

        public static final ApiResponseStatus UPDATE_PASSWORD_SUCCESS = new ApiResponseStatus(STATUS_SUCCESS,
                        "Update password successful");
        public static final ApiResponseStatus RESET_PASSWORD_SUCCESS = new ApiResponseStatus(STATUS_SUCCESS,
                        "Reset password successful");
        public static final ApiResponseStatus RETRIEVE_USER_SUCCESS = new ApiResponseStatus(STATUS_SUCCESS,
                        "Get User successful");
        public static final ApiResponseStatus RETRIEVE_ALL_USERS_SUCCESS = new ApiResponseStatus(STATUS_SUCCESS,
                        "Get All Users successful");

        public static final ApiResponseStatus REGISTRATION_SUCCESS = new ApiResponseStatus(STATUS_SUCCESS,
                        "User registered successfully");
        public static final ApiResponseStatus LOGIN_SUCCESS = new ApiResponseStatus(STATUS_SUCCESS,
                        "User login successful");

        public static final ApiResponseStatus LOGOUT_SUCCESS = new ApiResponseStatus(STATUS_SUCCESS,
                        "User logout successful");
        public static final ApiResponseStatus VERIFICATION_EMAIL_SENT = new ApiResponseStatus(STATUS_SUCCESS,
                        "Verification email sent successfully");

        public static final ApiResponseStatus VERIFY_EMAIL_SUCCESS = new ApiResponseStatus(STATUS_SUCCESS,
                        "Email verified successfully");

        public static final ApiResponseStatus UPDATE_USER_SUCCESSFULLY = new ApiResponseStatus(STATUS_SUCCESS,
                        "Update user successfully");
        public static final ApiResponseStatus DELETE_USER_SUCCESSFULLY = new ApiResponseStatus(STATUS_SUCCESS,
                        "Delete user successfully");
        public static final ApiResponseStatus REFRESH_TOKEN_SUCCESSFUL = new ApiResponseStatus(STATUS_SUCCESS,
                        "Refresh token successful");
        public static final ApiResponseStatus GENERATE_TOKEN_SUCCESSFUL = new ApiResponseStatus(STATUS_SUCCESS,
                        "Generate token successful");
        public static final ApiResponseStatus RETRIEVE_SEARCH_USERS_SUCCESS = new ApiResponseStatus(STATUS_SUCCESS,
                        "Get Search Users successful");
        public static final ApiResponseStatus UPDATE_USER_ADDRESS_SUCCESSFULLY = new ApiResponseStatus(STATUS_SUCCESS,
                        "Update user address successfully");
        public static final ApiResponseStatus AVATAR_UPDATED = new ApiResponseStatus(STATUS_SUCCESS,
                        "Update user's avatar successfully");
        public static final ApiResponseStatus PASSWORD_RESET_SENT_EMAIL = new ApiResponseStatus(STATUS_SUCCESS,
                        "Password reset sent to your email.");

        // SUCCESS RESPONSE END

        public static final ApiResponseStatus FAILURE = new ApiResponseStatus(STATUS_FAILURE,
                        "Error: Operation failed");

        public static final ApiResponseStatus USER_NOT_FOUND = new ApiResponseStatus(STATUS_FAILURE,
                        "Error: User not found");
        public static final ApiResponseStatus EMAIL_NOT_FOUND = new ApiResponseStatus(STATUS_FAILURE,
                        "Error: Email not found");
        public static final ApiResponseStatus ERROR_RETRIEVE_USER = new ApiResponseStatus(STATUS_FAILURE,
                        "Error: while retrieve user information");

        public static final ApiResponseStatus INVALID_REQUEST = new ApiResponseStatus(STATUS_FAILURE,
                        "Error: Invalid request");
        public static final ApiResponseStatus INVALID_USER_ID = new ApiResponseStatus(STATUS_FAILURE,
                        "Error: Invalid user's ID");

        public static final ApiResponseStatus INTERNAL_SERVER_ERROR = new ApiResponseStatus(STATUS_FAILURE,
                        "Error: Internal server error");

        public static final ApiResponseStatus ALREADY_LOGGED_OUT = new ApiResponseStatus(STATUS_FAILURE,
                        "Error: User is already logged out");

        public static final ApiResponseStatus REGISTRATION_FAILED = new ApiResponseStatus(STATUS_FAILURE,
                        "Error: User registration failed");

        public static final ApiResponseStatus VERIFICATION_EMAIL_FAILED = new ApiResponseStatus(STATUS_FAILURE,
                        "Error: Failed to send verification email");
        public static final ApiResponseStatus BAD_REQUEST = new ApiResponseStatus(STATUS_FAILURE,
                        "Error: Bad request");
        public static final ApiResponseStatus INVALID_CREDENTIALS = new ApiResponseStatus(STATUS_FAILURE,
                        "Error: Invalid credential");
        public static final ApiResponseStatus UNAUTHORIZED = new ApiResponseStatus(STATUS_FAILURE,
                        "Error: User is not authenticated");
        public static final ApiResponseStatus INVALID_EMAIL = new ApiResponseStatus(STATUS_FAILURE,
                        "Error: Email not found, please try again.");
        public static final ApiResponseStatus ERROR_UPDATING_PASSWORD = new ApiResponseStatus(STATUS_FAILURE,
                        "Error: Password update failed, please try again");
        public static final ApiResponseStatus EMAIL_NOT_VERIFIED = new ApiResponseStatus(STATUS_FAILURE,
                        "Error: Email not verified, please verify your email");
        public static final ApiResponseStatus ERROR_VERIFIED_EMAIL = new ApiResponseStatus(STATUS_FAILURE,
                        "Error: verified email");
        public static final ApiResponseStatus USER_ID_NULL = new ApiResponseStatus(STATUS_FAILURE,
                        "Error: User ID is null");
        public static final ApiResponseStatus REFRESH_TOKEN_HAS_EXPIRED = new ApiResponseStatus(STATUS_FAILURE,
                        "Error: Refresh token has expired");
        public static final ApiResponseStatus ACCESS_TOKEN_HAS_EXPIRED = new ApiResponseStatus(STATUS_FAILURE,
                        "Error: Access token has expired");
        public static final ApiResponseStatus INVALID_REFRESH_TOKEN = new ApiResponseStatus(STATUS_FAILURE,
                        "Error: Invalid or revoked refresh token");
        public static final ApiResponseStatus ERROR_LOGGING_OUT = new ApiResponseStatus(STATUS_FAILURE,
                        "Error: Error logging out");
        public static final ApiResponseStatus INVALID_INPUT = new ApiResponseStatus(STATUS_FAILURE,
                        "Error: Invalid input");
        public static final ApiResponseStatus INVALID_TOKEN = new ApiResponseStatus(STATUS_FAILURE,
                        "Error: Invalid token");
        public static final ApiResponseStatus UNEXPECTED_ERROR = new ApiResponseStatus(STATUS_FAILURE,
                        "Error: An unexpected error occurred");
        public static final ApiResponseStatus ACCESS_DENIED = new ApiResponseStatus(STATUS_FAILURE,
                        "Error: User does not have permission to access this resource");
        public static final ApiResponseStatus INVALID_PARAMETERS = new ApiResponseStatus(STATUS_FAILURE,
                        "Error: Provided parameters are invalid");
        public static final ApiResponseStatus USERNAME_EMAIL_NOT_FOUND = new ApiResponseStatus(STATUS_FAILURE,
                        "Error: User name or email not found");

}
