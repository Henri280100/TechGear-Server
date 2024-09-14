package com.v01.techgear_server.model;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
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
@Table(name = "user_address")
public class UserAddress {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long addressId;

    @Column(nullable = false)
    private String country;

    private Double latitude;
    private Double longitude;

    @Column(name = "address_details")
    private String addressDetails;

    @OneToOne
    @JoinColumn(name = "user_id") // Ensure the column name matches your database schema
    private User users;

    // Additional fields for MapBox API response
    @Transient
    private String type;

    @Transient
    private List<MapBoxFeature> features;

}
