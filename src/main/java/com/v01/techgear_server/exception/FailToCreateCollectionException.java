package com.v01.techgear_server.exception;

public class FailToCreateCollectionException extends RuntimeException {

    public FailToCreateCollectionException(String message) {
        super(message);
    }

    public FailToCreateCollectionException(String message, Throwable t) {
        super(message, t);
    }

}