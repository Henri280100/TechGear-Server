package com.v01.techgear_server.model;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "billing_information")
public class BillingInformation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer billingId;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "invoiceId")
    private Invoice invoice;

    // Billing Address Relationship
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "billing_address_id")
    private BillingAddress billingAddress;

    @OneToOne
    @JoinColumn(name = "account_details_id")
    private AccountDetails accountDetails;
    // Billing Preferences
    private boolean isPrimaryBillingInfo;
    private boolean isVerified;

    @Column(name="created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name="updated_at", updatable = false)
    private LocalDateTime updatedAt;
}