package com.v01.techgear_server.enums;

public class ApiResponseStatus {
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

        public static final ApiResponseStatus SUCCESS = new ApiResponseStatus("success", "Operation successful");

        public static final ApiResponseStatus UPDATE_PASSWORD_SUCCESS = new ApiResponseStatus("success",
                        "Update password successful");
        public static final ApiResponseStatus RESET_PASSWORD_SUCCESS = new ApiResponseStatus("success",
                        "Reset password successful");
        public static final ApiResponseStatus RETRIEVE_USER_SUCCESS = new ApiResponseStatus("success",
                        "Get User successful");
        public static final ApiResponseStatus RETRIEVE_ALL_USERS_SUCCESS = new ApiResponseStatus("success",
                        "Get All Users successful");

        public static final ApiResponseStatus REGISTRATION_SUCCESS = new ApiResponseStatus("success",
                        "User registered successfully");
        public static final ApiResponseStatus LOGIN_SUCCESS = new ApiResponseStatus("success", "User login successful");

        public static final ApiResponseStatus LOGOUT_SUCCESS = new ApiResponseStatus("success",
                        "User logout successful");
        public static final ApiResponseStatus VERIFICATION_EMAIL_SENT = new ApiResponseStatus("success",
                        "Verification email sent successfully");

        public static final ApiResponseStatus VERIFY_EMAIL_SUCCESS = new ApiResponseStatus("success",
                        "Email verified successfully");

        public static final ApiResponseStatus UPDATE_USER_SUCCESSFULLY = new ApiResponseStatus("success",
                        "Update user successfully");
        public static final ApiResponseStatus DELETE_USER_SUCCESSFULLY = new ApiResponseStatus("success",
                        "Delete user successfully");
        public static final ApiResponseStatus REFRESH_TOKEN_SUCCESSFUL = new ApiResponseStatus("success",
                        "Refresh token successful");
        public static final ApiResponseStatus GENERATE_TOKEN_SUCCESSFUL = new ApiResponseStatus("success",
                        "Generate token successful");
        public static final ApiResponseStatus RETRIEVE_SEARCH_USERS_SUCCESS = new ApiResponseStatus("success",
                        "Get Search Users successful");
        public static final ApiResponseStatus UPDATE_USER_ADDRESS_SUCCESSFULLY = new ApiResponseStatus("success",
                        "Update user address successfully");
        public static final ApiResponseStatus AVATAR_UPDATED = new ApiResponseStatus("success",
                        "Update user's avatar successfully");

        // SUCCESS RESPONSE END

        public static final ApiResponseStatus FAILURE = new ApiResponseStatus("failure", "Error: Operation failed");

        public static final ApiResponseStatus USER_NOT_FOUND = new ApiResponseStatus("failure",
                        "Error: User not found");
        public static final ApiResponseStatus ERROR_RETRIEVE_USER = new ApiResponseStatus("failure",
                        "Error: while retrieve user information");

        public static final ApiResponseStatus INVALID_REQUEST = new ApiResponseStatus("failure",
                        "Error: Invalid request");
        public static final ApiResponseStatus INVALID_USER_ID = new ApiResponseStatus("failure",
                        "Error: Invalid user's ID");

        public static final ApiResponseStatus INTERNAL_SERVER_ERROR = new ApiResponseStatus("failure",
                        "Error: Internal server error");

        public static final ApiResponseStatus ALREADY_LOGGED_OUT = new ApiResponseStatus("failure",
                        "Error: User is already logged out");

        public static final ApiResponseStatus REGISTRATION_FAILED = new ApiResponseStatus("failure",
                        "Error: User registration failed");

        public static final ApiResponseStatus VERIFICATION_EMAIL_FAILED = new ApiResponseStatus("failure",
                        "Error: Failed to send verification email");
        public static final ApiResponseStatus BAD_REQUEST = new ApiResponseStatus("failure",
                        "Error: Bad request");
        public static final ApiResponseStatus INVALID_CREDENTIALS = new ApiResponseStatus("failure",
                        "Error: Invalid credential");
        public static final ApiResponseStatus UNAUTHORIZED = new ApiResponseStatus("failure",
                        "Error: User is not authenticated");
        public static final ApiResponseStatus INVALID_EMAIL = new ApiResponseStatus("failure",
                        "Error: Email not found, please try again.");
        public static final ApiResponseStatus ERROR_UPDATING_PASSWORD = new ApiResponseStatus("failure",
                        "Error: Password update failed, please try again");
        public static final ApiResponseStatus EMAIL_NOT_VERIFIED = new ApiResponseStatus("failure",
                        "Error: Email not verified, please verify your email");
        public static final ApiResponseStatus ERROR_VERIFIED_EMAIL = new ApiResponseStatus("failure",
                        "Error: verified email");
        public static final ApiResponseStatus USER_ID_NULL = new ApiResponseStatus("failure",
                        "Error: User ID is null");
        public static final ApiResponseStatus REFRESH_TOKEN_HAS_EXPIRED = new ApiResponseStatus("failure",
                        "Error: Refresh token has expired");
        public static final ApiResponseStatus ACCESS_TOKEN_HAS_EXPIRED = new ApiResponseStatus("failure",
                        "Error: Access token has expired");
        public static final ApiResponseStatus INVALID_REFRESH_TOKEN = new ApiResponseStatus("failure",
                        "Error: Invalid or revoked refresh token");
        public static final ApiResponseStatus ERROR_LOGGING_OUT = new ApiResponseStatus("failure",
                        "Error: Error logging out");
        public static final ApiResponseStatus INVALID_INPUT = new ApiResponseStatus("failure",
                        "Error: Invalid input");
        public static final ApiResponseStatus INVALID_TOKEN = new ApiResponseStatus("failure",
                        "Error: Invalid token");
        public static final ApiResponseStatus UNEXPECTED_ERROR = new ApiResponseStatus("failure",
                        "Error: An unexpected error occurred");
        public static final ApiResponseStatus ACCESS_DENIED = new ApiResponseStatus("failure",
                        "Error: User does not have permission to access this resource");
        public static final ApiResponseStatus INVALID_PARAMETERS = new ApiResponseStatus("failure",
                        "Error: Provided parameters are invalid");
        public static final ApiResponseStatus USERNAME_EMAIL_NOT_FOUND = new ApiResponseStatus("failure",
                        "Error: User name or email not found");

}
