package com.v01.techgear_server.product.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.v01.techgear_server.enums.ProductAvailability;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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

    @Schema(description = "Product minimum price")
    private BigDecimal productMinPrice;

    @Schema(description = "Product maximum price")
    private BigDecimal productMaxPrice;

    @Schema(description = "Product availability", example = "AVAILABLE")
    private ProductAvailability productAvailability;

    @Schema(description = "Product stock level", example = "10")
    private int productStockLevel;

    @Schema(description = "Product brand", example = "Asus")
    private String productBrand;

    @Schema(description = "Product image")
    private String productImage;

    @Schema(description = "product features")
    private String productFeatures;

    @Schema(description = "Product category", example = "Electronics")
    private String productCategory;

    @Schema(description = "Product detail price")
    private BigDecimal productDetailPrice;


    @Schema(description = "Product Tags")
    @JsonProperty("productTags")
    private List<String> productTags = new ArrayList<>();

    @Schema(description = "Product details")
    private List<ProductDetailDTO> productDetails;

    public ProductDTO(Long id, String productName, String productDescription,
                      BigDecimal productDetailPrice, BigDecimal productMinPrice, BigDecimal productMaxPrice,
                      String productAvailability, int productStockLevel,
                      String productBrand, String productImage, String productFeatures, String productCategory) {
        this.id = id;
        this.productName = productName;
        this.productDescription = productDescription;
        this.productDetailPrice = productDetailPrice != null ? productDetailPrice : BigDecimal.ZERO;
        this.productMinPrice = productMinPrice != null ? productMinPrice : BigDecimal.ZERO;
        this.productMaxPrice = productMaxPrice != null ? productMaxPrice : BigDecimal.ZERO;
        this.productAvailability = ProductAvailability.fromValue(productAvailability);
        this.productStockLevel = productStockLevel;
        this.productBrand = productBrand;
        this.productImage = productImage;
        this.productFeatures = productFeatures;
        this.productCategory = productCategory;
        this.productTags = new ArrayList<>();
    }
}
