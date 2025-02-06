package com.v01.techgear_server.exception;

public class ProductFilteringException extends RuntimeException {
    public ProductFilteringException(String message) {
        super(message);
    }

    public ProductFilteringException(String message, Throwable cause) {
        super(message, cause);
    }

}