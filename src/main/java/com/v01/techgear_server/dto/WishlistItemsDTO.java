package com.v01.techgear_server.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WishlistItemsDTO {
    
    @Schema(description = "Wishlist item ID", example = "1")
    @Positive(message = "Wishlist item ID must be a positive number")
    private Long id;
    
    @Schema(description = "Product name", example = "Apple iPhone")
    private String productName;
    
    @Schema(description = "Product price")
    private Double price;
    
    @Schema(description = "Product image")
    private ImageDTO imageUrl;
    
    @Schema(description = "Notes about the wishlist item")
    private String notes;
    
    @Schema(description = "Product associated with the wishlist item")
    private ProductDTO product;
    
    @Schema(description = "Wishlist associated with the wishlist item")
    private WishlistDTO wishlist;
}
