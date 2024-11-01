package com.v01.techgear_server.utils;

import com.v01.techgear_server.dto.ApiResponse;
import com.v01.techgear_server.enums.ApiResponseStatus;

public class ApiResponseBuilder {
    public static <T> ApiResponse<T> createResponse(boolean isSuccess, ApiResponseStatus status, T data) {
        ApiResponse<T> apiResponse = new ApiResponse<>();
        apiResponse.setSuccess(isSuccess);
        apiResponse.setStatus(status.getStatus());
        apiResponse.setMessage(status.getMessage());
        apiResponse.setData(data); // Set the data directly
        return apiResponse;
    }
    
    public static <T> ApiResponse<T> createSuccessResponse(T data, ApiResponseStatus status) {
        return createResponse(true, status, data);
    }

    public static <T>ApiResponse<T> createErrorResponse(ApiResponseStatus status) {

        return createResponse(false, status, null);

    }

    public static <T>ApiResponse<T> createErrorResponse(ApiResponseStatus status, T errorData) {

        return createResponse(false, status, errorData);

    }

//     public static ApiResponse createErrorResponse(ApiResponseStatus status, Object errorData) {

//         return createResponse(false, status, errorData);

//     }
}
