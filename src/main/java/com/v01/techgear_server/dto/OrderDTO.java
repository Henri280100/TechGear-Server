package com.v01.techgear_server.dto;

import java.time.LocalDateTime;

import com.v01.techgear_server.enums.OrderStatus;

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
@Schema(description = "Order DTO")
public class OrderDTO {

    @Schema(description = "Unique identifier of the order")
    @Positive(message = "Order ID must be a positive number")
    private Long orderId;

    @Schema(description = "Status of the order")
    private OrderStatus orderStatus;

    @Schema(description = "Date and time when the order was placed")
    private LocalDateTime orderDate;

    @Schema(description = "Details of the account associated with the order")
    private AccountDetailsDTO accountDetails;
}
