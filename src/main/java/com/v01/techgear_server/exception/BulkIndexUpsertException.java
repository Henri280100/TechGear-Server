package com.v01.techgear_server.exception;

public class BulkIndexUpsertException extends RuntimeException {
    public BulkIndexUpsertException(String message) {
        super(message);
    }

    public BulkIndexUpsertException(String message, Throwable t) {
        super(message, t);
    }
}