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
    private String description;

    @Schema(description = "Product price")
    private double price;

    @Schema(description = "Product availability", example = "AVAILABLE")
    private ProductAvailability availability;

    @Schema(description="Product stock level", example = "10")
    private int stockLevel;
    
    @Schema(description = "Product slug", example = "apple-iphone")
    private String slug;

    @Schema(description = "Product image")
    private ImageDTO image;

    @Schema(description = "Product category", example = "Electronics")
    private String category;
}
