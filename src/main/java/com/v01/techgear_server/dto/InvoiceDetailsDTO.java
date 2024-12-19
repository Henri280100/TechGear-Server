package com.v01.techgear_server.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Invoice details Data transfer object")
public class InvoiceDetailsDTO {

    @Schema(description = "Product name")
    private String productName;

    @Schema(description = "Product quantity")
    private Integer quantity;

    @Schema(description = "Line item total")
    private BigDecimal lineTotal;

    @Schema(description = "Applied discount percentage")
    private BigDecimal discountPercentage;
}