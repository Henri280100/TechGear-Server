package com.v01.techgear_server.dto;

import com.v01.techgear_server.enums.ProductAvailability;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    @Schema(description = "Product ID", example = "1")
    private Long id;

    @Schema(description = "Product name", example = "Apple iPhone")
    private String name;
    @Schema(description = "Product description", example = "This is a description of the Apple iPhone")
    private String productDescription;

    @Schema(description = "Product price")
    private double productPrice;

    @Schema(description = "Product availability", example = "AVAILABLE")
    private ProductAvailability productAvailability;

    @Schema(description="Product stock level", example = "10")
    private int productStockLevel;
    
    @Schema(description = "Product slug", example = "apple-iphone")
    private String productSlug;

    @Schema(description = "Product image")
    private ImageDTO productImage;

    @Schema(description = "Product category", example = "Electronics")
    private String productCategory;
}
