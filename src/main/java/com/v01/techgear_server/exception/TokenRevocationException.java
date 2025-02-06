package com.v01.techgear_server.exception;

public class TokenRevocationException extends RuntimeException {
    public TokenRevocationException(String message, Throwable cause) {
        super(message, cause);
    }
}