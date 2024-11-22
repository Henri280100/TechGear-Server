package com.v01.techgear_server.exception;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message, Throwable t) {
        super(message, t);
    }

    public BadRequestException(String message) {
        super(message);
    }
}
