package com.v01.techgear_server.dto;

import com.v01.techgear_server.enums.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderSummaryDTO {

    @Schema(description = "Order Summary ID")
    private Long orderSummaryId;

    @Schema(description = "Sub total")
    private BigDecimal subTotal;

    @Schema(description = "Shipping cost")
    private BigDecimal shippingCost;

    @Schema(description = "Total amount")
    private BigDecimal totalAmount;

    @Schema(description = "Order date")
    private LocalDateTime orderDate;

    @Schema(description = "Order status")
    private OrderStatus orderStatus;

    @Schema(description = "Notes")
    private String notes;

    @Schema(description = "Currency")
    private String currency;

    @Schema(description = "Created at")
    private LocalDateTime createdAt;

    @Schema(description = "Updated at")
    private LocalDateTime updatedAt;

    @Schema(description = "Account details associated with the order summary")
    private AccountDetailsDTO accountDetails;

}
