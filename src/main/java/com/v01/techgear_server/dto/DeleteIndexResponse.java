package com.v01.techgear_server.dto;

import java.util.HashMap;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeleteIndexResponse {
    private Long productId;
    private boolean success;
    private String message;
    private String errorCode;
    private String errorDetails;
    private HashMap<String, Object> deletedDocument;
}