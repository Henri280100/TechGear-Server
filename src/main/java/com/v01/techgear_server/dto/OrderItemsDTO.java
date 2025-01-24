package com.v01.techgear_server.dto;

import com.v01.techgear_server.enums.OrderItemStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Order Items data transfer object")
public class OrderItemsDTO {
    @Schema(description = "Order Item ID", example = "123")
    @Positive(message = "Order Item ID must be a positive number")
    private Integer id;

    @Schema(description = "Quantity")
    private Integer itemsQuantity;

    @Schema(description = "Order item unit price")
    private Double itemsUnitPrice;

    @Schema(description = "Order associated with the Order item")
    private OrderDTO orderDTO;

    @Schema(description = "Product associated with the Order item")
    private ProductDTO productDTO;

    @Schema(description = "Order item status")
    @Enumerated(EnumType.STRING)
    private OrderItemStatus status;
}