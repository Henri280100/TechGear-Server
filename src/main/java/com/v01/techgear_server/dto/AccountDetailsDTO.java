package com.v01.techgear_server.dto;

import java.time.LocalDateTime;

import com.v01.techgear_server.enums.UserTypes;

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
@Schema(description = "Account Details")
public class AccountDetailsDTO {
    @Schema(description = "Account ID")
    @Positive(message = "Account ID must be a positive number")
    private Integer accountDetailsId;

    @Schema(description = "Email verified")
    private boolean emailVerified;

    @Schema(description="Phone number verified")
    private boolean phoneNumberVerified;

    @Schema(description = "Account creation timestamp")
    private LocalDateTime registrationDate;

    @Schema(description="Account last updated timestamp")
    private LocalDateTime accountUpdatedTime;

    @Schema(description = "two factor authentication enabled")
    private boolean twoFactorAuthEnabled;

    @Schema(description="Tax id")
    private String taxId;

    @Schema(description = "User Types")
    private UserTypes userTypes;

    @Schema(description="User associated with the account")
    private UserDTO user;

    @Schema(description="Billing information")
    private BillingInformationDTO billingInformation;
}