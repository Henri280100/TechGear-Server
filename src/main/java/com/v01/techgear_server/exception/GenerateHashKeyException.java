package com.v01.techgear_server.exception;

public class GenerateHashKeyException extends RuntimeException {

    public GenerateHashKeyException(String message) {
        super(message);
    }

    public GenerateHashKeyException(String message, Throwable cause) {
        super(message, cause);
    }

}