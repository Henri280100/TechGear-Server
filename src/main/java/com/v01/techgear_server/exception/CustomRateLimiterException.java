package com.v01.techgear_server.exception;

public class CustomRateLimiterException extends RuntimeException{
    public CustomRateLimiterException(String message) {
        super(message);
    }

    public CustomRateLimiterException(String message, Throwable cause) {
        super(message, cause);
    }
}