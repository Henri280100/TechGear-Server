package com.v01.techgear_server.order.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.v01.techgear_server.enums.OrderItemStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Order Items data transfer object")
public class OrderItemsDTO {
    @Schema(description = "Order Item ID", example = "123")
    @Positive(message = "Order Item ID must be a positive number")
    private Long id;

    @Schema(description = "Quantity", example = "2")
    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be a positive number")
    private Integer itemsQuantity;

    @Schema(description = "Order item unit price", example = "49.99")
    @NotNull(message = "Unit price is required")
    @Positive(message = "Order item unit price must be a positive number")
    @Digits(integer = 10, fraction = 2, message = "Unit price must have up to 2 decimal places")
    private BigDecimal itemsUnitPrice;

    @Schema(description = "Order ID", example = "10010")
    @NotNull(message = "Order ID is required")
    @Positive(message = "Order ID must be a positive number")
    private Long orderId;

    @Schema(description = "Product name", example = "Wireless Mouse")
    @NotBlank(message = "Product name cannot be blank")
    private String productName;

    @Schema(description = "Order summary ID", example = "5005")
    @NotNull(message = "Order summary ID is required")
    @Positive(message = "Order summary ID must be a positive number")
    private Long orderSummaryId;

    @Schema(description = "Order item status", example = "CONFIRMED")
    @NotNull(message = "Order item status is required")
    private OrderItemStatus status;

    @Schema(description = "Order item creation timestamp", example = "2024-02-04T12:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @Schema(description = "Order item last updated timestamp", example = "2024-02-05T10:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime lastUpdatedAt;
}