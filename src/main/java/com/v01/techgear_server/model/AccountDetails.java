package com.v01.techgear_server.model;

import java.time.LocalDateTime;
import java.util.*;
import com.v01.techgear_server.enums.UserTypes;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "account_details")
public class AccountDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer accountDetailsId;

    @Column(name = "is_email_verified")
    private boolean emailVerified;

    @Column(name = "phone_number_verified")
    private boolean phoneNumberVerified;

    @Column(name = "account_creation_timestamp")
    private LocalDateTime registrationDate;

    @Column(name = "account_updated_timestamp")
    private LocalDateTime accountUpdatedTime;

    @Column(name = "last_login_timestamp")
    private LocalDateTime lastLoginTime;

    @Column(name = "last_updated_timestamp")
    private LocalDateTime lastUpdatedTimestamp;

    @Column(name = "two_factor_auth_enabled")
    private boolean twoFactorAuthEnabled;

    @Column(name = "userTypes")
    @Enumerated(EnumType.STRING)
    private UserTypes userTypes;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User users;

    // Bidirectional relationship with BillingInformation
    @OneToOne(mappedBy = "accountDetails", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private BillingInformation billingInformation;

    @OneToMany(mappedBy = "account_details")
    private List<Review> reviews;

    @OneToMany(mappedBy = "account_details")
    private List<Wishlist> wishlists;

    @OneToMany(mappedBy = "account_details", cascade = CascadeType.ALL)
    private List<Order> order;

    @OneToMany(mappedBy = "account_details")
    private List<PaymentMethod> paymentMethods;

    @OneToMany(mappedBy = "account_details")
    private List<Invoice> invoices;

    @PrePersist
    public void onCreate() {
        registrationDate = LocalDateTime.now();
        accountUpdatedTime = LocalDateTime.now();
        lastLoginTime = LocalDateTime.now();
        lastUpdatedTimestamp = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        accountUpdatedTime = LocalDateTime.now();
        lastUpdatedTimestamp = LocalDateTime.now();
    }

}
