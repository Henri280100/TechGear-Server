package com.v01.techgear_server.dto;
import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "WishlistDTO", description = "Wishlist DTO")
public class WishlistDTO {
    
    @Schema(description = "Wishlist ID", example = "1")
    private Long wishlistId;

    @Schema(description = "Wishlist description")
    private String wishlistDescription;

    @Schema(description = "Wishlist created date")
    private LocalDateTime createdDate;

    @Schema(description = "Wishlist last updated date")
    private LocalDateTime lastUpdatedDate;

    @Schema(description = "Total value of the wishlist")
    private double totalValue;

    @Schema(description = "Priority of the wishlist")
    private Integer priority;

    @Schema(description = "Notify sale")
    private boolean notifySale;

    @Schema(description = "Wishlist items DTO")
    private List<WishlistItemsDTO> items;
}
