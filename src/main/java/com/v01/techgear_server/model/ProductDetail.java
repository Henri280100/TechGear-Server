package com.v01.techgear_server.model;

import java.util.ArrayList;
import java.util.List;

import com.v01.techgear_server.enums.ProductAvailability;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "product_detail")
public class ProductDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "warranty")
    private String warranty;

    @Enumerated(EnumType.STRING)
    @Column(name = "product_availability")
    private ProductAvailability availability;

    @Column(name = "voucherCode")
    private String voucherCode;

    @Column(name = "technicalSpecifications")
    private String technicalSpecifications;

    @Column(name = "description")
    private String description;

    @OneToMany(mappedBy = "product")
    private List<Review> reviews;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "product_id")
    private Product product;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InvoiceDetails> invoiceDetails = new ArrayList<>();

    @OneToMany(mappedBy = "productDetails", cascade = CascadeType.ALL)
    private List<ProductSpecification> specifications;
}
