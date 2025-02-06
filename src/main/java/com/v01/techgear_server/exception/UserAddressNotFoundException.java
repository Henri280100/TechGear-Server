package com.v01.techgear_server.exception;

public class UserAddressNotFoundException extends RuntimeException{

    public UserAddressNotFoundException(String message, Throwable t) {
        super(message, t);
    }

    public UserAddressNotFoundException(String message) {
        super(message);
    }
}