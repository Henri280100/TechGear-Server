package com.v01.techgear_server.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductFilterSortRequest {
    private Long id;
    private String name;
    private double productPrice;
    private double minPrice;
    private double maxPrice;
    private String category;
    private String brand;
    private Integer page;
    private Integer size;
    private String sort;
    
}