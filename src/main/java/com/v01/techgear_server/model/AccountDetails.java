package com.v01.techgear_server.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.v01.techgear_server.enums.UserGenders;
import com.v01.techgear_server.enums.UserTypes;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
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
public class AccountDetails implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long accountDetailsId;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @OneToOne(mappedBy = "account_details", cascade = CascadeType.ALL)
    private UserPhoneNo phoneNumbers;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id", referencedColumnName = "addressId")
    @JsonManagedReference
    private UserAddress addresses;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "image_id")
    private Image userAvatar;

    @Column(name = "is_email_verified")
    private boolean isEmailVerified;

    @Column(name = "is_phone_number_verified")
    private boolean isPhoneNumberVerified;

    @Column(name = "account_creation_timestamp")
    private LocalDateTime registrationDate;

    @Column(name = "account_updated_timestamp")
    private LocalDateTime accountUpdatedTime;

    @Column(name = "last_login_timestamp")
    private LocalDateTime lastLoginTime;

    @Column(name = "last_updated_timestamp")
    private LocalDateTime lastUpdatedTimestamp;

    @Column(name = "userTypes")
    @Enumerated(EnumType.STRING)
    private UserTypes userTypes;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User users;

    @OneToMany(mappedBy = "account_details")
    private List<Wishlist> wishlists;

    @OneToMany(mappedBy = "account_details", cascade = CascadeType.ALL)
    private List<Order> order;

    @OneToMany(mappedBy = "account_details")
    private List<Invoice> invoices;

    @OneToMany(mappedBy = "accountDetails", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PaymentMethod> paymentMethods;

    @Column(name = "total_reviews")
    private int totalReviews;

    @Column(name = "account_age")
    private Long accountAgeDays;

    @Enumerated(EnumType.STRING)
    private UserGenders genders;

    @Column(name = "dob")
    private LocalDateTime dateOfBirth;

    @OneToMany(mappedBy = "accountDetails", cascade = CascadeType.ALL)
    private List<ProductRating> productRatings;

    @OneToMany(mappedBy = "accountDetails", cascade = CascadeType.ALL)
    private List<ShipperRating> shipperRatings;

    @OneToMany(mappedBy = "accountDetails", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderSummary> orderSummaries;

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

    // Add Method for Account Age Calculation
    public Long calculateAccountAge() {
        return ChronoUnit.DAYS.between(registrationDate, LocalDateTime.now());
    }

    // Business logic methods
    public void addPaymentMethod(PaymentMethod paymentMethod) {
        if (paymentMethods == null) {
            paymentMethods = new ArrayList<>();
        }
        paymentMethods.add(paymentMethod);
        paymentMethod.setAccountDetails(this);
    }
}
