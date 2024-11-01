package com.v01.techgear_server.exception;

public class UserRolesNotFoundException extends RuntimeException {
    public UserRolesNotFoundException(String message) {
        super(message);
    }

    public UserRolesNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}