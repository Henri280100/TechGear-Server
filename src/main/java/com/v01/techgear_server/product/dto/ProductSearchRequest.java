package com.v01.techgear_server.product.dto;

import com.v01.techgear_server.shared.model.search.SortOption;
import com.v01.techgear_server.enums.ProductAvailability;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class ProductSearchRequest {
    private String query;
    private List<ProductCategoryDTO> categories;
    private BigDecimal finalPrice;
    private List<String> brands;
    private Double minPrice;
    private Double maxPrice;
    private ProductAvailability availability;
    private Integer minStockLevel;
    private Integer page;
    private Integer perPage;
    private List<SortOption> sortOption;
}
