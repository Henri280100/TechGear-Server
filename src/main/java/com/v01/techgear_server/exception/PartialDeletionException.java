package com.v01.techgear_server.exception;

import java.util.List;

public class PartialDeletionException extends RuntimeException {
    
    private List<String> failedDeletionsList;

    public PartialDeletionException(String message, List<String> failedDeletions) {
        super(message);
        this.failedDeletionsList = failedDeletions;
    }
    
    public PartialDeletionException(String message) {
        super(message);
    }
    public PartialDeletionException(String message, Throwable cause) {
        super(message, cause);
    }
}
