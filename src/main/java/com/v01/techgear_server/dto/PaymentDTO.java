package com.v01.techgear_server.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.v01.techgear_server.enums.PaymentStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Payment Data Transfer Object")
public class PaymentDTO {
    @Schema(description = "Payment ID", example = "123")
    private Long paymentId;

    @Schema(description = "Stripe Payment Intent ID")
    private String stripePaymentIntentId;

    @Schema(description = "Invoice associated with the payment")
    private InvoiceDTO paymentInvoice;

    @Schema(description = "Payment amount")
    private BigDecimal paymentAmount;

    @Schema(description = "Payment date")
    private LocalDateTime paymentDateTime;

    @Schema(description = "Payment status")
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @Schema(description = "Account details associated with the payment")
    private AccountDetailsDTO userAccountDetails;

    @Schema(description = "Order summary associated with the payment")
    private OrderSummaryDTO summaryDTO;

}
