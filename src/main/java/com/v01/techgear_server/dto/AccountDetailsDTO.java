package com.v01.techgear_server.dto;

import java.util.stream.Collectors;
import java.time.LocalDateTime;
import java.util.List;

import com.v01.techgear_server.enums.UserTypes;
import com.v01.techgear_server.model.AccountDetails;

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
@Schema(description = "Account Details Data Transfer Object")
public class AccountDetailsDTO {
    @Schema(description = "Account ID")
    @Positive(message = "Account ID must be a positive number")
    private Integer accountDetailsId;

    @Schema(description = "Email verified")
    private boolean emailVerified;

    @Schema(description = "Phone number verified")
    private boolean phoneNumberVerified;

    @Schema(description = "Account creation timestamp")
    private LocalDateTime registrationDate;

    @Schema(description = "Account last updated timestamp")
    private LocalDateTime accountUpdatedTime;

    @Schema(description = "last_login_timestamp")
    private LocalDateTime lastLoginTimeStamp;

    @Schema(description = "two factor authentication enabled")
    private boolean twoFactorAuthEnabled;

    @Schema(description = "User Types")
    private UserTypes userTypes;

    @Schema(description = "User associated with the account")
    private UserDTO user;

    @Schema(description = "Billing information")
    private BillingInformationDTO billingInformationDTO;

    @Schema(description = "Payment associated with the account details")
    private PaymentDTO payment;

    @Schema(description = "Bank accounts associated with the account details")
    private List<BankAccountDTO> bankAccount;

    public static AccountDetailsDTO fromEntity(AccountDetails entity) {
        return entity == null ? null
                : AccountDetailsDTO.builder()
                        .accountDetailsId(entity.getAccountDetailsId())
                        .emailVerified(entity.isEmailVerified())
                        .phoneNumberVerified(entity.isPhoneNumberVerified())
                        .registrationDate(entity.getRegistrationDate())
                        .accountUpdatedTime(entity.getAccountUpdatedTime())
                        .lastLoginTimeStamp(entity.getLastLoginTime())
                        .twoFactorAuthEnabled(entity.isTwoFactorAuthEnabled())
                        .userTypes(entity.getUserTypes())
                        .user(UserDTO.fromEntity(entity.getUsers()))
                        .billingInformation(BillingInformationDTO.fromEntity(entity.getBillingInformation()))
                        .payment(PaymentDTO.fromEntity(entity.getPayment()))
                        .bankAccount(entity.getBankAccounts().stream()
                                .map(BankAccountDTO::fromEntity)
                                .collect(Collectors.toList()))
                        .build();
    }

    public AccountDetails toEntity() {
        AccountDetails entity = new AccountDetails();
        entity.setAccountDetailsId(accountDetailsId);
        entity.setEmailVerified(emailVerified);
        entity.setPhoneNumberVerified(phoneNumberVerified);
        entity.setRegistrationDate(registrationDate);
        entity.setAccountUpdatedTime(accountUpdatedTime);
        entity.setLastLoginTime(lastLoginTimeStamp);
        entity.setTwoFactorAuthEnabled(twoFactorAuthEnabled);
        entity.setUserTypes(userTypes);
        entity.setUsers(user.toEntity());
        entity.setBillingInformation(billingInformationDTO.toEntity());
        entity.setPayment(payment.toEntity());
        entity.setBankAccount(bankAccount.stream().map(BankAccountDTO::toEntity).collect(Collectors.toList()));
        return entity;
    }
}