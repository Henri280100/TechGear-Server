package com.v01.techgear_server.dto;

import java.util.List;

import com.v01.techgear_server.enums.Category;
import com.v01.techgear_server.enums.ProductAvailability;
import com.v01.techgear_server.model.SortOption;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductSearchRequest {
    private String query;
    private List<Category> categories;
    private List<String> brands;
    private Double minPrice;
    private Double maxPrice;
    private ProductAvailability availability;
    private Integer minStockLevel;
    private Integer page;
    private Integer perPage;
    private List<SortOption> sortOption;
}
