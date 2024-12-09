package com.v01.techgear_server.dto;

import lombok.*;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Payment method Data Transfer Object")
public class PaymentMethodDTO {
    @Schema(description = "Payment Method ID", example = "123")
    @Positive(message = "Payment Method ID must be a positive number")
    private Integer paymentMethodId;

    @Schema(description = "Stripe payment method ID")
    private String stripePaymentMethodId;

    @Schema(description = "Payment type")
    private String type;

    @Schema(description = "Last four digits")
    private String lastFourDigits;

    @Schema(description = "Card brand")
    private String cardBrand;

    @Schema(description = "Expiration date")
    private LocalDateTime expirationDate;

    @Schema(description = "Is default")
    private Boolean isDefault;

    @Schema(description = "Payment status")
    private String paymentStatus;

    @Schema(description = "Account details associated with the payment method")
    private AccountDetailsDTO accountDetails;

}