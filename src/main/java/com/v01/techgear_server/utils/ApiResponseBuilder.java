package com.v01.techgear_server.utils;

import com.v01.techgear_server.common.dto.ApiResponseDTO;
import com.v01.techgear_server.enums.ApiResponseStatus;

public class ApiResponseBuilder {
    public static <T> ApiResponseDTO<T> createResponse(boolean isSuccess, ApiResponseStatus status, T data) {
        ApiResponseDTO<T> apiResponse = new ApiResponseDTO<>();
        apiResponse.setSuccess(isSuccess);
        apiResponse.setStatus(status.getStatus());
        apiResponse.setMessage(status.getMessage());
        apiResponse.setData(data); // Set the data directly
        return apiResponse;
    }
    
    public static <T> ApiResponseDTO<T> createSuccessResponse(T data, ApiResponseStatus status) {
        return createResponse(true, status, data);
    }

    public static <T>ApiResponseDTO<T> createErrorResponse(ApiResponseStatus status) {

        return createResponse(false, status, null);

    }

    public static <T>ApiResponseDTO<T> createErrorResponse(ApiResponseStatus status, T errorData) {

        return createResponse(false, status, errorData);

    }


//     public static ApiResponse createErrorResponse(ApiResponseStatus status, Object errorData) {

//         return createResponse(false, status, errorData);

//     }
}
