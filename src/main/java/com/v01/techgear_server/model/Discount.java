package com.v01.techgear_server.model;

import lombok.*;

import com.v01.techgear_server.enums.DiscountType;

import jakarta.persistence.*;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "discounts")
public class Discount {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long discountId;

    @Column(name = "discount_percentage")
    private Double discountPercentage;

    @Column(name = "discount_code")
    private String discountCode;

    @Column(name = "discount_name")
    private String discountName;

    @Column(name = "is_discount_active")
    private String isDiscountActive;

    @Column(name = "discount_type")
    @Enumerated(EnumType.STRING)
    private DiscountType discountType;

    @Column(name = "discount_description")
    private String discountDescription;

    @Column(name = "discount_status")
    private String discountStatus;

    @Column(name = "discount_limit")
    private Integer discountLimit;

    @Column(name = "start_date")
    private String startDate;

    @Column(name = "expiry_date")
    private String expiryDate;
}