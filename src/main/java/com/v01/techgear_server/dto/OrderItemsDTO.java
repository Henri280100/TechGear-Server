package com.v01.techgear_server.dto;

import java.math.BigDecimal;

import com.v01.techgear_server.enums.OrderItemStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description= "Order Items data transfer object")
public class OrderItemsDTO {
    @Schema(description = "Order Item ID", example = "123")
    @Positive(message = "Order Item ID must be a positive number")
    private Long orderItemId;

    @Schema(description = "Order history associated with the Order item")
    private OrderHistoryDTO orderHistoryDTO;

    @Schema(description = "Product associated with the Order item")
    private InvoiceDTO invoiceDTO;

    @Schema(description = "Quantity")
    private Integer quantity;

    @Schema(description="Order item unit price")
    private BigDecimal unitPrice;

    @Schema(description="Order item status")
    @Enumerated(EnumType.STRING)
    private OrderItemStatus orderItemStatus;
}