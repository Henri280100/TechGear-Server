package com.v01.techgear_server.model;

import lombok.*;
import jakarta.persistence.*;
import java.util.*;
import java.math.BigDecimal;
import java.time.*;

@Data
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bank_account")
public class BankAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer bankAccountId;

    @Column(name = "account_number", nullable = false, unique = true)
    private String accountNumber;

    @Column(name = "account_holder_name", nullable = false)
    private String accountHolderName;

    @Column(name = "bank_name", nullable = false)
    private String bankName;

    @Column(name = "account_type", nullable = false)
    private String accountType; // e.g., "Savings", "Checking"

    @Column(name = "balance", nullable = false)
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(name = "currency", nullable = false)
    private String currency; // e.g., "USD", "EUR"

    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate = LocalDateTime.now();

    @Column(name = "status", nullable = false)
    private String status; // e.g., "Active", "Inactive"

    @ManyToOne
    @JoinColumn(name = "account_details_id", nullable = false)
    private AccountDetails accountDetails; // Assuming you have a User entity

    @OneToMany(mappedBy = "bankAccount", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PaymentMethod> paymentMethods = new ArrayList<>();
}