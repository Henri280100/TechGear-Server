package com.v01.techgear_server.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.v01.techgear_server.enums.AddressTypes;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
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
@Table(name = "user_address")
public class UserAddress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long addressId;

    
    @Column(name = "address_line_1")
    private String addressLineOne;
    
    @Column(name="address_line_2")
    private String addressLineTwo;
    
    @Column(name="city", nullable = false)
    private String city;
    
    @Column(name="state_province")
    private String stateProvince;

    @Column(name="country" ,nullable = false)
    private String country;

    @Column(name="zipPostalCode")
    private String zipPostalCode;
    
    @Column(name="addressType")
    @Enumerated(EnumType.STRING)
    private AddressTypes addressType;
    
    @Column(name="is_address_primary")
    private boolean primaryAddress;

    @OneToOne(mappedBy = "addresses", cascade = CascadeType.ALL)
    @JsonBackReference
    private User user;
    
    @Column(name = "created_at")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

}
