package com.v01.techgear_server.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "specification")
public class ProductSpecification {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name="specsName")
    private String specsName;
    @Column(name="icon")
    private String icon;

    @OneToOne(mappedBy = "specifications")
    private Product product;
}
