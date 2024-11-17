package com.v01.techgear_server.model;

import lombok.*;
import jakarta.persistence.*;

@Embeddable
@Data
public class ShippingDetails {
    @Column(name="shipping_username")
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

}