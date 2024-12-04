package com.v01.techgear_server.dto;

import java.util.*;
import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceDTO {
    @Schema(description = "Invoice ID")
    @Positive(message = "Invoice ID must be a positive number")
    private Long invoiceId;

    @Schema(description = "Invoice details associated with the invoice")
    private List<InvoiceDetailsDTO> invoiceDetailsDTO;

    @Schema(description = "Order items associated with the invoice")
    private List<OrderItemsDTO> orderItemsDTO;

    @Schema(description="Billing information associated with the invoice")
    private List<BillingInformationDTO> billingInformationDTO;

    @Schema(description = "Account details associated with the invoice")
    private AccountDetailsDTO accountDetails;

    @Schema(description="Payment associated with the invoice")
    private List<PaymentDTO> payments;

    @Schema(description="User associated with invoice")
    private UserDTO userDTO;

    @Schema(description="Invoice date")
    private String invoiceDate;

    @Schema(description = "Invoice Total")
    private Integer totalAmount;
}