package com.v01.techgear_server.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WishlistItemsDTO {
    
    @Schema(description = "Wishlist item ID", example = "1")
    private Long id;
    
    @Schema(description = "Product name", example = "Apple iPhone")
    private String productName;
    
    @Schema(description = "Product price")
    private Double price;
    
    @Schema(description = "Notes about the wishlist item")
    private String notes;
}
