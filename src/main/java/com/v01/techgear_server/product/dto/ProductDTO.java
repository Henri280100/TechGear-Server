package com.v01.techgear_server.product.dto;

import com.v01.techgear_server.common.dto.ImageDTO;
import com.v01.techgear_server.enums.Category;
import com.v01.techgear_server.enums.ProductAvailability;

import com.v01.techgear_server.enums.ProductStatus;
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
    private String productName;

    @Schema(description = "Product description", example = "This is a description of the Apple iPhone")
    private String productDescription;

    @Schema(description = "Product price")
    private double productPrice;

    @Schema(description = "Product minimum price")
    private double productMinPrice;

    @Schema(description = "Product maximum price")
    private double productMaxPrice;

    @Schema(description = "Product availability", example = "AVAILABLE")
    private String productAvailability;

    @Schema(description="Product stock level", example = "10")
    private int productStockLevel;
    
    @Schema(description = "Product brand", example = "Asus")
    private String productBrand;

    @Schema(description = "Product image")
    private String productImage;

    @Schema(description = "product features")
    private String productFeatures;

    @Schema(description = "Product category", example = "Electronics")
    private String productCategory;
}
