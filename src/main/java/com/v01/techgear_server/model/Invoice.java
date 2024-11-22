package com.v01.techgear_server.model;

import java.time.LocalDateTime;
import java.util.*;
import lombok.*;
import jakarta.persistence.*;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "invoice")
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long invoiceId;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<InvoiceDetails> invoiceDetails = new ArrayList<>();

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<OrderItems> orderItems = new ArrayList<>();

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<BillingInformation> billingInformation = new ArrayList<>();

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "accountDetailsId")
    private AccountDetails accountDetails;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Payment> payments = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User user;

    /**
     * Represents the date of the invoice.
     */
    @Column(name = "invoice_date")
    private LocalDateTime invoiceDate;

    /**
     * Represents the total amount of the invoice.
     */
    @Column(name = "total_amount")
    private Integer totalAmount;

    @OneToMany
    @JoinColumn(name = "paymentId")
    private Payment payment;

}
