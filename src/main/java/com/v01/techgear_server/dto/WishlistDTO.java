package com.v01.techgear_server.dto;
import java.time.*;
import java.util.*;
import lombok.*;
import jakarta.persistence.*;
import io.swagger.v3.oas.annotations.media.Schema;

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

    @Schema(description = "Image associated with the wishlist")
    private ImageDTO wishlistImage;

    @Schema(description = "Notify sale")
    private boolean notifySale;

    @Schema(description = "Wishlist items DTO")
    private List<WishlistItemsDTO> items;

    @Schema(description = "Account details associated with the wishlist")
    private AccountDetailsDTO accountDetails;
}
