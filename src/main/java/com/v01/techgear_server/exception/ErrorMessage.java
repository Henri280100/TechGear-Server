package com.v01.techgear_server.exception;

import java.time.LocalDateTime;

public class ErrorMessage {
    private String message;
    private int status;
    private LocalDateTime timestamp;
    private String description;


    public ErrorMessage(String message, int status, String description) {
        this.message = message;
        this.status = status;
        this.description = description;
        this.timestamp = LocalDateTime.now();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    

}
