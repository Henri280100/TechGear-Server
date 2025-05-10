package com.v01.techgear_server.wishlist.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.v01.techgear_server.shared.dto.ImageDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "WishlistDTO", description = "Wishlist DTO")
public class WishlistDTO {

    @Schema(description = "Wishlist ID", example = "1")
    private Long wishlistId;

    @Schema(description = "Wishlist description", example = "My birthday wishlist")
    private String wishlistDescription;

    @Schema(description = "Wishlist created date", example = "2024-02-01T12:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdDate;

    @Schema(description = "Wishlist last updated date", example = "2024-02-10T15:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime lastUpdatedDate;

    @Schema(description = "Total value of the wishlist", example = "199.99")
    @PositiveOrZero(message = "Total value must be zero or positive")
    private BigDecimal totalValue;

    @Schema(description = "Priority of the wishlist", example = "2")
    @PositiveOrZero(message = "Priority must be zero or positive")
    private Integer priority;

    @Schema(description = "Wishlist image")
    private ImageDTO image;

    @Schema(description = "Notify sale", example = "true")
    private boolean notifySale;

    @Schema(description = "Wishlist items DTO")
    private List<WishlistItemsDTO> items;
}
