package com.v01.techgear_server.dto;

import java.util.List;

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
    private String isDiscountActive;

    @Schema(description = "Discount type", example = "PERCENTAGE")
    private DiscountType discountType;

    @Schema(description = "Discount description", example = "Summer 2022 Discount description")
    private String discountDescription;

    @Schema(description = "Discount status", example = "ACTIVE")
    private String discountStatus;

    @Schema(description = "Discount limit", example = "100")
    private Integer discountLimit;

    @Schema(description = "Start date of the discount", example = "2022-05-01")
    private String startDate;

    @Schema(description = "Expiry date of the discount", example = "2022-08-31")
    private String expiryDate;

    @Schema(description = "Products associated with the discount")
    private List<ProductDTO> products;

    @Schema(description = "Invoice details associated with the discount")
    private List<InvoiceDetailsDTO> invoiceDetails;
}
