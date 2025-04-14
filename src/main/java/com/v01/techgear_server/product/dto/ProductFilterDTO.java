package com.v01.techgear_server.product.dto;

import java.util.List;

import com.v01.techgear_server.enums.SortDirection;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductFilterDTO {
    private String name;
    private Double minPrice;
    private Double maxPrice;
    // Exact Match Filters
    private String category;
    private String brand;
    
    // Multiple Categories
    private List<String> categories;
    
    // Sorting
    private String sortField;
    private SortDirection sortDirection;
    
    // Pagination
    private Integer page;
    private Integer size;
    
}