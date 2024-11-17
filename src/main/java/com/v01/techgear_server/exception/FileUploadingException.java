package com.v01.techgear_server.exception;

public class FileUploadingException extends RuntimeException {
    public FileUploadingException(String message) {
        super(message);
    }

    public FileUploadingException(String message, Throwable cause) {
        super(message, cause);
    }
}