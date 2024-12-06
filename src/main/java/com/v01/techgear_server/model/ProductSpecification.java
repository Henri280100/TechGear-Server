package com.v01.techgear_server.model;

import java.io.Serializable;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "specification")
public class ProductSpecification implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long specId;

    @Column(name="spec_name")
    private String specsName;

    @Column(name="spec_value")
    private String specValue;

    @Column(name="spec_image")
    private Image specImage;

    @Column(name="icon")
    private String icon;

    @ManyToOne
    @JoinColumn(name = "product_details_id")
    private ProductDetail productDetails;
}
