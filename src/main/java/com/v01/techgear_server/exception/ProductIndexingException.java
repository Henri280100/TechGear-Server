package com.v01.techgear_server.exception;

public class ProductIndexingException extends RuntimeException {
    public ProductIndexingException(String message) {
        super(message);
    }

    public ProductIndexingException(String message, Throwable t) {
        super(message, t);
    }
}
