package com.v01.techgear_server.dto;

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

    public ApiResponseDTO(T data, String message, boolean success, String status) {
        this.data = data;
        this.message = message;
        this.success = success;
        this.status = status;
    }


}