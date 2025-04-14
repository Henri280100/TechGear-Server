package com.v01.techgear_server.invoice.dto;

import java.util.List;

import com.v01.techgear_server.enums.InvoiceStatus;

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
public class InvoiceDTO {
    @Schema(description = "Invoice ID")
    @Positive(message = "Invoice ID must be a positive number")
    private Long invoiceId;

    @Schema(description = "Invoice status")
    private InvoiceStatus invoiceStatus;

    @Schema(description = "Invoice number")
    private String invoiceNumber;

    @Schema(description = "Issue date of the invoice")
    private String invoiceIssueDate;

    @Schema(description = "Total amount of the invoice")
    private Integer invoiceTotalAmount;

    @Schema(description = "Details of the invoice")
    private List<InvoiceDetailsDTO> invoiceDetails;
}