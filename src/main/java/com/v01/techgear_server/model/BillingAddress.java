package com.v01.techgear_server.model;

import com.v01.techgear_server.enums.AddressTypes;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "billing_address")
public class BillingAddress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer billingAddressId;

    @Column(name = "address_line1")
    private String addressLine1;

    @Column(name = "address_line2")
    private String addressLine2;

    @Column(name = "city")
    private String city;

    @Column(name = "state_province")
    private String stateProvince;

    @Column(name = "postal_code")
    private String postalCode;

    @Column(name = "country")
    private String country;

    @Enumerated(EnumType.STRING)
    private AddressTypes addressType;

    // Bidirectional relationship with BillingInformation
    @OneToOne(mappedBy = "billingAddress")
    private BillingInformation billingInformation;
}
