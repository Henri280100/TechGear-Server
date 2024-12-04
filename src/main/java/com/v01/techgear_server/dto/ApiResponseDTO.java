package com.v01.techgear_server.dto;

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

    public T getData() {
    return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}