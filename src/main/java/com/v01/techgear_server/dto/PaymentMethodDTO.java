package com.v01.techgear_server.dto;

import lombok.*;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

import com.v01.techgear_server.enums.PaymentCardNetwork;
import com.v01.techgear_server.enums.PaymentMethodType;
import com.v01.techgear_server.model.PaymentMethod;

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

    @Schema(description = "Payment method types")
    private PaymentMethodType paymentMethodType;

    @Schema(description = "Masked number")
    private String maskedNumber;

    @Schema(description = "Card holder name")
    private String cardHolderName;

    @Schema(description = "Expiry month")
    private Integer expiryMonth;

    @Schema(description = "Expiry year")
    private Integer expiryYear;

    @Schema(description = "Card network")
    private PaymentCardNetwork cardNetwork;

    @Schema(description = "Is primary payment method")
    private boolean isPrimaryPaymentMethod;

    @Schema(description = "Is verified")
    private boolean isVerified;

    @Schema(description = "Payment Token")
    private String paymentToken;

    @Schema(description = "Last used")
    private LocalDateTime lastUsed;

    public static PaymentMethodDTO fromEntity(PaymentMethod paymentMethod) {
        if (paymentMethod == null) {
            return null;
        }

        return PaymentMethodDTO.builder()
                .paymentMethodId(paymentMethod.getPaymentMethodId())
                .paymentMethodType(paymentMethod.getPaymentMethodType())
                .maskedNumber(paymentMethod.getMaskedNumber())
                .cardHolderName(paymentMethod.getCardHolderName())
                .expiryMonth(paymentMethod.getExpiryMonth())
                .expiryYear(paymentMethod.getExpiryYear())
                .cardNetwork(paymentMethod.getCardNetwork())
                .isPrimaryPaymentMethod(paymentMethod.isPrimaryPaymentMethod())
                .isVerified(paymentMethod.isVerified())
                .paymentToken(paymentMethod.getPaymentToken())
                .lastUsed(paymentMethod.getLastUsed())
                .build();
    }

    public PaymentMethod toEntity() {
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setPaymentMethodId(paymentMethodId);
        paymentMethod.setPaymentMethodType(paymentMethodType);
        paymentMethod.setMaskedNumber(maskedNumber);
        paymentMethod.setCardHolderName(cardHolderName);
        paymentMethod.setExpiryMonth(expiryMonth);
        paymentMethod.setExpiryYear(expiryYear);
        paymentMethod.setCardNetwork(cardNetwork);
        paymentMethod.setPrimaryPaymentMethod(isPrimaryPaymentMethod);
        paymentMethod.setVerified(isVerified);
        paymentMethod.setPaymentToken(paymentToken);
        paymentMethod.setLastUsed(lastUsed);

        return paymentMethod;

    }
}