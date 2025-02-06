package com.v01.techgear_server.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WishlistItemsDTO {

    @Schema(description = "Wishlist item ID", example = "1")
    @NotNull(message = "Wishlist item ID is required")
    @Positive(message = "Wishlist item ID must be a positive number")
    private Long id;

    @Schema(description = "Product ID", example = "1001")
    private Long productId;

    @Schema(description = "Product price", example = "999.99")
    @NotNull(message = "Price is required")
    @Digits(integer = 10, fraction = 2, message = "Price must have up to 10 digits and 2 decimal places")
    private BigDecimal price;

    @Schema(description = "Notes about the wishlist item", example = "Gift for my birthday")
    @Size(max = 255, message = "Notes should not exceed 255 characters")
    private String notes;
}


