package com.v01.techgear_server.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description="Invoice details Data transfer object")
public class InvoiceDetailsDTO {
    @Schema(description="Invoice details ID")
    @Positive(message="Invoice details must be a positive number")
    private Integer invoiceDetailsId;

    @Schema(description="Invoice associated with the invoice details")
    private InvoiceDTO invoiceDTO;

    @Schema(description="Product associated with the invoice details")
    private ProductDTO productDTO;

    @Schema(description="Discount associated with the invoice details")
    private DiscountDTO discountDTO;

    @Schema(description = "Invoice details line total")
    private BigDecimal lineTotal;
}