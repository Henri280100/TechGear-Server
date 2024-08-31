package com.v01.techgear_server.dto;

import com.v01.techgear_server.enums.ProductAvailability;

import lombok.Data;

@Data
public class ProductDetailDTO {
    private Long id;
    private String warranty;
    private ProductAvailability availability;
    private String voucherCode;
    private String technicalSpecifications;
    private String description;
}
