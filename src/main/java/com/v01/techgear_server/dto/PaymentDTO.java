package com.v01.techgear_server.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Positive;

import java.util.*;
import com.v01.techgear_server.enums.PaymentStatus;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Payment Data Transfer Object")
public class PaymentDTO {
    @Schema(description = "Payment ID")
    @Positive(message="Payment ID must be a positive number")
    private Integer paymentId;

    @Schema(description = "Invoice associated with the payment")
    private InvoiceDTO invoiceDTO;

    @Schema(description="Payment amount")
    private BigDecimal paymentAmount;

    @Schema(description = "Payment date")
    private LocalDateTime paymentDate;

    @Schema(description="Payment logs associated with the payment")
    private List<PaymentLogsDTO> logs = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @Schema(description = "Payment method associated with the payment")
    private PaymentMethodDTO paymentMethod;

    @Schema(description="Account details associated with the payment")
    private AccountDetailsDTO accountDetails;
    
    public static PaymentDTO fromEntity(Payment payment) {
        if (payment == null) {
            return null;
        }

        return PaymentDTO.builder()
        .paymentId(payment.getPaymentId())
        .invoice(InvoiceDTO.fromEntity(payment.getInvoice()))
        .paymentAmount(payment.getPaymentAmount())
        .paymentDate(payment.getPaymentDate())
        .logs(payment.getLogs())
        .paymentStatus(payment.getPaymentStatus())
        .paymentMethod(payment.getPaymentMethod())
        .accountDetails(AccountDetailsDTO.fromEntity(payment.getAccountDetails()))
        .build();
    }
}
