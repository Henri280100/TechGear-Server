package com.v01.techgear_server.product.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductFilterSortResponse {
    private Long id;
    private String name;
    private double productPrice;
    private double minPrice;
    private double maxPrice;
    private String category;
    private String brand;
}