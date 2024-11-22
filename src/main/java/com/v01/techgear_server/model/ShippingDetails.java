package com.v01.techgear_server.model;

import java.util.*;

import java.time.LocalDateTime;

import jakarta.persistence.OneToOne;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "shipping_details")
public class ShippingDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer shippingDetailsId;

    @Column(name = "shipping_username")
    private String username;

    @Column(name = "shipping_address_line1")
    private String addressLine1;

    @Column(name = "shipping_address_line2")
    private String addressLine2;

    @Column(name = "shipping_city")
    private String city;

    @Column(name = "shipping_state")
    private String state;

    @Column(name = "shipping_postal_code")
    private String postalCode;

    @Column(name = "shipping_country")
    private String country;

    @Column(name = "shipping_phone")
    private String phoneNumber;

    @Column(name = "shipping_email")
    private String email;

    @Column(name = "shipping_instructions")
    private String specialInstructions;

    @Column(name = "shipping_date")
    private LocalDateTime shippingDate;

    @Column(name = "estimated_delivery_date")
    private LocalDateTime estimatedDeliveryDate;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "shippingMethodId")
    private ShippingMethod shippingMethod;

    @OneToMany(mappedBy = "shippingDetails", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Invoice> invoice= new ArrayList<>();

    @OneToOne(mappedBy = "shippingDetails", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private OrderHistory orderHistory;
}