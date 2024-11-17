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
    
    // User Association
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accountDetailsId", nullable = false)
    private AccountDetails accountDetail;

    // Personal Information Relationship
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "accountDetailsId")
    private AccountDetails accountDetails;

    // Billing Address Relationship
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "billing_address_id")
    private BillingAddress billingAddress;

    // Payment Methods
    @OneToMany(mappedBy = "billingInformation", 
               cascade = CascadeType.ALL, 
               fetch = FetchType.LAZY)
    private List<PaymentMethod> paymentMethods;

    // Billing Preferences
    private boolean isPrimaryBillingInfo;
    private boolean isVerified;

    // Audit Fields
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}