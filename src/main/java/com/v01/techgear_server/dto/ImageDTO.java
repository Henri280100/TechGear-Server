package com.v01.techgear_server.dto;

import lombok.Data;

@Data
public class ImageDTO {
    private Long id;
    private String fileName;
    private String contentType;
    private byte[] data;
}
