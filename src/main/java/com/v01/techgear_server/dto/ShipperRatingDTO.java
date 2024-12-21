package com.v01.techgear_server.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShipperRatingDTO {

    @Schema(description = "Shipper Rating ID", example = "1")
    private Long id;

    @Schema(description = "Comments", example = "Great service")
    private String comments;

    @Schema(description = "Rating", example = "4.5")
    private double rating;

    @Schema(description = "Rating Date", example = "2021-01-01T00:00:00")
    private LocalDateTime ratingDateTime;

    @Schema(description = "Shipper")
    private ShipperDTO shipper;

    @Schema(description = "Account Details")
    private AccountDetailsDTO accountDetails;
}
