package com.v01.techgear_server.dto;

import com.v01.techgear_server.enums.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderSummaryDTO {

    @Schema(description = "Unique identifier of the order summary", example = "1")
    private Long orderSummaryId;

    @Schema(description = "Date and time when the order was placed", example = "2023-10-01T12:00:00")
    private LocalDateTime orderDate;

    @Schema(description = "Current status of the order", example = "PENDING")
    private OrderStatus orderStatus;

    @Schema(description = "Shipping cost of the order", example = "10.00")
    private Double subTotal;

    @Schema(description = "Total amount of the order", example = "110.00")
    private BigDecimal totalAmount;

    @Schema(description = "Currency used for the order", example = "USD")
    private String currency;

    @Schema(description = "Notes for the order", example = "Please deliver before 5 PM")
    private String notes;

    @Schema(description="Created at", example = "2023-10-01T12:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "List of items in the order")
    private List<OrderItemsDTO> orderItems;
}
