package com.v01.techgear_server.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.v01.techgear_server.enums.CommunicationPreference;
import com.v01.techgear_server.enums.PhoneNumberPurpose;
import com.v01.techgear_server.enums.PhoneNumberType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "userPhoneNos", uniqueConstraints = { @UniqueConstraint(columnNames = { "phoneNo", "countryCode" }) })
public class UserPhoneNo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "phoneNo")
    private String phoneNo;

    @Column(name = "countryCode")
    private String countryCode;

    @Column(name = "phone_number_type")
    @Enumerated(EnumType.STRING)
    private PhoneNumberType type;

    @Column(name = "phone_number_purpose")
    @Enumerated(EnumType.STRING)
    private PhoneNumberPurpose purpose;

    @Column(name = "is_phone_number_verified")
    private boolean phoneNumberVerified;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @Column(name = "is_phone_number_primary")
    private boolean primary;

    @Column(name = "marketing_consent")
    private boolean marketingConsent;

    @Column(name="two_factor_auth_enabled")
    private boolean twoFactorAuthEnabled;

    @Column(name = "communication_preference")
    @Enumerated(EnumType.STRING)
    private CommunicationPreference communicationPreference;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "marketingConsentAt")
    private LocalDateTime marketingConsentAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Convenience Methods
    public void verify() {
        this.phoneNumberVerified = true;
        this.verifiedAt = LocalDateTime.now();
    }

    public void updateMarketingConsent(boolean consent) {
        this.marketingConsent = consent;
        this.marketingConsentAt = LocalDateTime.now();
    }

    public String getFormattedPhoneNumber() {
        return countryCode + " " + phoneNo;
    }

    public String getMaskedPhoneNumber() {
        if (phoneNo == null || phoneNo.length() < 4) {
            return phoneNo;
        }
        return countryCode + " ****" + phoneNo.substring(phoneNo.length() - 4);
    }

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    @JsonIgnore
    private User users;

}
