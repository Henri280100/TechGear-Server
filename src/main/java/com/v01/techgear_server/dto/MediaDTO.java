package com.v01.techgear_server.dto;

import lombok.Data;

@Data
public class MediaDTO {
    private Long id;
    private String mediaFilename;
    private String mediaContentType;
    private byte[] data;
}