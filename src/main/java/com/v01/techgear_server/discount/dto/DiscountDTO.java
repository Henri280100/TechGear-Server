package com.v01.techgear_server.discount.dto;

import com.v01.techgear_server.enums.DiscountType;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Data Transfer Object for Discount")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiscountDTO {
    @Schema(description = "Discount ID", example = "123")
    private Long discountId;

    @Schema(description = "Discount percentage", example = "10.0")
    private Double discountPercentage;

    @Schema(description = "Discount code", example = "SUMMER_20")
    private String discountCode;

    @Schema(description = "Discount name", example = "Summer 2022 Discount")
    private String discountName;

    @Schema(description = "Is discount active", example = "true")
    private Boolean isDiscountActive;

    @Schema(description = "Discount type", example = "PERCENTAGE")
    private DiscountType discountType;

    @Schema(description = "Start date of the discount", example = "2022-05-01")
    private String startDate;

    @Schema(description = "Expiry date of the discount", example = "2022-08-31")
    private String expiryDate;
}
