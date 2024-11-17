package com.v01.techgear_server.model;


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

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "specification_id")
    private ProductSpecification specifications;

    @Column(name="voucherCode")
    private String voucherCode;

    @Column(name="technicalSpecifications")
    private String technicalSpecifications;

    @Column(name = "description")
    private String description;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "product_id")
    private Product product;
}
