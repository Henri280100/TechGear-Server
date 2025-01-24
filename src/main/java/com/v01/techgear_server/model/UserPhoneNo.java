package com.v01.techgear_server.model;

import com.v01.techgear_server.enums.CommunicationPreference;
import com.v01.techgear_server.enums.PhoneNumberPurpose;
import com.v01.techgear_server.enums.PhoneNumberType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@ToString
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "userPhoneNos", uniqueConstraints = { @UniqueConstraint(columnNames = { "phoneNo", "countryCode" }) })
public class UserPhoneNo implements Serializable {
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
    
    @Column(name = "communication_preference")
    @Enumerated(EnumType.STRING)
    private CommunicationPreference communicationPreference;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accountDetailsId")
    @ToString.Exclude
    private AccountDetails accountDetails;

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

    @Override
    public final boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null)
            return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy
                ? ((HibernateProxy) o).getHibernateLazyInitializer()
                                      .getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy
                ? ((HibernateProxy) this).getHibernateLazyInitializer()
                                         .getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass)
            return false;
        UserPhoneNo that = (UserPhoneNo) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer()
                                                                       .getPersistentClass()
                                                                       .hashCode() : getClass().hashCode();
    }
}
