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

	@Schema(description = "Invoice details ID")
	private Integer invoiceDetailsId;

	@Schema(description="Invoice associated with the invoice details")
	private InvoiceDTO invoice;

	@Schema(description = "Product associated with the invoice details")
	private ProductDTO product;

	@Schema(description = "Discount associated with the invoice details")
	private DiscountDTO discount;

	@Schema(description = "Line item total")
	private BigDecimal lineTotal;

}