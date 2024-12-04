package com.v01.techgear_server.dto;

import java.util.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

import com.v01.techgear_server.model.BankAccount;

import lombok.*;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Bank Account data transfer object")
public class BankAccountDTO {

    @Schema(description = "Bank account id")
    @Positive(message = "Bank account id must be a positive number")
    private Integer bankAccountId;

    @Schema(description = "Account number")
    private String accountNumber;

    @Schema(description = "Account holder name")
    private String accountHolderName;

    @Schema(description = "Bank name")
    private String bankName;

    @Schema(description = "Account type")
    private String accountType;

    @Schema(description = "Balance")
    private BigDecimal balance;

    @Schema(description = "Currency")
    private String currency;

    @Schema(description = "Created date")
    private LocalDateTime createdDate;

    @Schema(description = "Status")
    private String status;

    @Schema(description = "Account details associated with the bank account")
    private AccountDetailsDTO accountDetailsDTO;

    @Schema(description = "Payment method")
    private List<PaymentMethodDTO> paymentMethodDTO;

    public static BankAccountDTO fromEntity(BankAccount entity) {
        return entity == null ? null
                : BankAccountDTO.builder().bankAccountId(entity.getBankAccountId())
                        .accountNumber(entity.getAccountNumber())
                        .accountHolderName(entity.getAccountHolderName())
                        .bankName(entity.getBankName())
                        .accountType(entity.getAccountType())
                        .balance(entity.getBalance())
                        .currency(entity.getCurrency())
                        .createdDate(entity.getCreatedDate())
                        .status(entity.getStatus())
                        .accountDetailsDTO(AccountDetailsDTO.fromEntity(entity.getAccountDetails()))
                        .paymentMethodDTO(entity.getPaymentMethods().stream()
                                .map(PaymentMethodDTO::fromEntity)
                                .collect(Collectors.toList()))
                        .build();
    }

    public BankAccount toEntity() {
        BankAccount bankAccount = new BankAccount();
        bankAccount.setBankAccountId(this.bankAccountId);
        bankAccount.setAccountNumber(this.accountNumber);
        bankAccount.setAccountHolderName(this.accountHolderName);
        bankAccount.setBankName(this.bankName);
        bankAccount.setAccountType(this.accountType);
        bankAccount.setBalance(this.balance);
        bankAccount.setCurrency(this.currency);        
        bankAccount.setCreatedDate(this.createdDate);
        bankAccount.setStatus(this.status);
        bankAccount.setAccountDetails(this.accountDetailsDTO.toEntity());
        bankAccount.setPaymentMethods(this.paymentMethodDTO.stream()
                .map(PaymentMethodDTO::toEntity)
                .collect(Collectors.toList()));
        return bankAccount;
    }
}