package com.v01.techgear_server.dto;

import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductRatingDTO {

    @Schema(description = "Product Rating ID", example = "1")
    private Long productRatingId;

    @Schema(description = "Comments", example = "Good product")
    private String comments;

    @Schema(description = "Rating", example = "4.5")
    private double rating;

    @Schema(description = "Rating Date", example = "2021-01-01T00:00:00")
    private LocalDateTime ratingDate;

    @Schema(description = "Review Image")
    private ImageDTO reviewImage;

    @Schema(description = "Account Details")
    private AccountDetailsDTO accountDetails;

    @Schema(description = "Product")
    private ProductDTO product;
}