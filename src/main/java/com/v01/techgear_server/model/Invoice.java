package com.v01.techgear_server.model;

import java.time.LocalDateTime;
import java.util.*;

import com.v01.techgear_server.enums.InvoiceStatus;

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

    @OneToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @Enumerated(EnumType.STRING)
    private InvoiceStatus stauts;

    @Column(name = "invoice_number")
    private String invoiceNumber;

    @Column(name = "issue_date")
    private String issueDate;

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

    @ManyToOne
    @JoinColumn(name = "account_details_id", nullable = false)
    private AccountDetails accountDetails;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<InvoiceDetails> invoiceDetails = new ArrayList<>();

}
