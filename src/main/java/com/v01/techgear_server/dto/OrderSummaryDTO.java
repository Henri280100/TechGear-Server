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

    @Schema(description = "Unique identifier of the order summary", example = "1")
    private Long orderSummaryId;

    @Schema(description = "Total amount of the order", example = "110.00")
    private BigDecimal totalAmount;

    @Schema(description = "Date and time when the order was placed", example = "2023-10-01T12:00:00")
    private LocalDateTime orderDate;

    @Schema(description = "Current status of the order", example = "PENDING")
    private OrderStatus orderStatus;

    @Schema(description = "Currency used for the order", example = "USD")
    private String currency;
}
