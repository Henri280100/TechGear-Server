package com.v01.techgear_server.exception;

public class ProductSearchException extends RuntimeException {
    public ProductSearchException(String message) {
        super(message);
    }

    public ProductSearchException(String message, Throwable t) {
        super(message, t);
    }
}
