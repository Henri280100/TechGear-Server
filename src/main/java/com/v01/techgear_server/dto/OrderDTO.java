package com.v01.techgear_server.dto;

import java.time.*;
import java.util.*;

import com.v01.techgear_server.enums.OrderStatus;

import lombok.*;
import jakarta.persistence.*;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Order DTO")
public class OrderDTO {

    @Schema(description = "Order ID", example = "123")
    private Long orderId;

    @Schema(description = "Total amount of the order")
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Schema(description = "Order status")
    private OrderStatus status;

    @Schema(description = "Order date")
    private LocalDateTime orderDate;

    @Schema(description = "Order items associated with the order")
    private List<OrderItemsDTO> orderItems;

    @Schema(description = "Payment associated with the order")
    private PaymentDTO payment;

    @Schema(description = "Shipper associated with the order")
    private ShipperDTO shipper;

    @Schema(description = "Shipping details associated with the order")
    private ShippingDetailsDTO shippingDetail;

    @Schema(description = "Invoice associated with the order")
    private InvoiceDTO invoice;

    @Schema(description = "Account details associated with the order")
    private AccountDetailsDTO accountDetails;

}
