package com.v01.techgear_server.exception;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
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


}
