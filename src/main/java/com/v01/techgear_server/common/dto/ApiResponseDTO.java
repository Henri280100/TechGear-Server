package com.v01.techgear_server.common.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ApiResponseDTO<T> {
    private T data; // Use a generic type for data
    private String message;
    private boolean success;
    private String status;

    public ApiResponseDTO() {
    }
}