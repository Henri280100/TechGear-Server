package com.v01.techgear_server.model;

import java.time.LocalDateTime;

import com.v01.techgear_server.enums.PaymentCardNetwork;
import com.v01.techgear_server.enums.PaymentMethodType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "payment_method")
public class PaymentMethod {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer paymentMethodId;

    @ManyToOne
    @JoinColumn(name = "billing_info_id")
    private BillingInformation billingInformation;

    @Enumerated(EnumType.STRING)
    private PaymentMethodType paymentMethodType;

    @Column(name = "masked_number")
    private String maskedNumber;

    @Column(name = "card_holder_name")
    private String cardHolderName;

    @Column(name = "expiry_month")
    private Integer expiryMonth;

    @Column(name = "expiry_year")
    private Integer expiryYear;

    @Enumerated(EnumType.STRING)
    private PaymentCardNetwork cardNetwork;

    private boolean isPrimaryPaymentMethod;
    private boolean isVerified;

    @Column(name = "token")
    private String paymentToken;

    private LocalDateTime lastUsed;

}