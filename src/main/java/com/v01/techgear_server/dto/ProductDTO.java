package com.v01.techgear_server.dto;

import java.util.List;

import com.v01.techgear_server.enums.Category;
import com.v01.techgear_server.enums.ProductAvailability;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    @Schema(description = "Product ID", example = "1")
    private Long productId;

    @Schema(description = "Product name", example = "Apple iPhone")
    private String name;

    @Schema(description = "Product description", example = "This is a description of the Apple iPhone")
    private String productDescription;

    @Schema(description = "Product availability")
    @Enumerated(EnumType.STRING)
    private ProductAvailability availability;

    @Schema(description = "Product image")
    private ImageDTO image;

    @Schema(description = "Product price")
    private double price;

    @Schema(description = "Product SKU", example = "ABC123")
    private String sku;

    @Schema(description = "Product category")
    @Enumerated(EnumType.STRING)
    private Category category;

    @Schema(description = "Product detail")
    private ProductDetailDTO productDetail;

    @Schema(description = "List of product ratings")
    private List<ProductRatingDTO> productRatings;

    @Schema(description = "List of order items")
    private List<OrderItemsDTO> orderItems;

    @Schema(description = "List of wishlist items")
    private List<WishlistItemsDTO> wishlistItems;
}
