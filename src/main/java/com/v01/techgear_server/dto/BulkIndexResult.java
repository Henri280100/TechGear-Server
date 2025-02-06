package com.v01.techgear_server.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BulkIndexResult {
    private int totalDocuments;
    private int successCount;
    private int errorCount;
    private List<String> errorMessages;
}