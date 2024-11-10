package com.v01.techgear_server.exception;

public class RedisConnectionException extends RuntimeException {
    public RedisConnectionException(String message) {
        super(message);
    }

    public RedisConnectionException(Throwable cause) {
        super(cause);
    }
}