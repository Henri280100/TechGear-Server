package com.v01.techgear_server.dto;

import java.util.*;

import com.v01.techgear_server.model.BillingInformation;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import jakarta.validation.constraints.Positive;
import io.swagger.v3.oas.annotations.media.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Billing Infrormation Data Transfer Object")
public class BillingInformationDTO {
    @Schema(description = "Billing ID")
    @Positive(message = "Billing ID must be a positive number")
    private Integer billingId;

    @Schema(description = "User associated with the billing information")
    private AccountDetailsDTO accountDetailsDTO;

    @Schema(description = "Account details associated with the billing information")
    private AccountDetailsDTO accountDetails;

    @Schema(description = "Billing address associated with the billing information")
    private BillingAddressDTO billingAddress;

    @Schema(description = "Payment method associated with the billing information")
    private List<PaymentMethodDTO> paymentMethod;

    @Schema(description = "Is payment method verified")
    private boolean isPrimaryBillingInfo;

    @Schema(description = "Is verified")
    private boolean isVerified;

    @Schema(description = "Last update timestamp")
    private LocalDateTime updatedAt;
    @Schema(description = "Creation timestamp")
    private LocalDateTime createdAt;


    public static BillingInformationDTO fromEntity(BillingInformation entity) {
        if (entity == null) return null;

        return BillingInformationDTO.builder().
        billingId(entity.getBillingId()).
        accountDetailsDTO(UserDTO.fromEntity(entity.))
    }

    public BillingInformation toEntity() {}
}