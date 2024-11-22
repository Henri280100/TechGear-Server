package com.v01.techgear_server.model;

import java.util.List;

import com.v01.techgear_server.enums.DiscountType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @JoinTable(name = "product_discount", joinColumns = @JoinColumn(name = "discount_id"), inverseJoinColumns = @JoinColumn(name = "product_id"))
    private List<Product> products; // Relationship with Product

    @OneToMany(mappedBy = "discount", cascade = CascadeType.ALL)
    private List<InvoiceDetails> invoiceDetails;
}