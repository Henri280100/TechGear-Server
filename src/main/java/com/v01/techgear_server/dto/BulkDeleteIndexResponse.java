package com.v01.techgear_server.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BulkDeleteIndexResponse {
    private boolean success;
    private int totalProducts;
    private int successfulDeletions;
    private int failedDeletions;
    private String message;
    private String errorCode;
    private List<DeleteIndexResponse> individualResponses;
}
