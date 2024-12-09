package com.v01.techgear_server.dto;

import java.time.LocalDateTime;
import java.util.*;

import com.v01.techgear_server.enums.InvoiceStatus;

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

    @Schema(description = "Order associated with the invoice")
    private OrderDTO order;

    @Schema(description = "Invoice status")
    private InvoiceStatus status;

    @Schema(description = "Invoice number")
    private String invoiceNumber;

    @Schema(description = "Issue date of the invoice")
    private String issueDate;

    @Schema(description = "Invoice date")
    private LocalDateTime invoiceDate;

    @Schema(description = "Total amount of the invoice")
    private Integer totalAmount;

    @Schema(description = "Account details associated with the invoice")
    private AccountDetailsDTO accountDetails;

    @Schema(description = "Details of the invoice")
    private List<InvoiceDetailsDTO> invoiceDetails;
}