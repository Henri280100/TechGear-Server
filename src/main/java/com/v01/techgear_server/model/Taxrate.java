package com.v01.techgear_server.model;

import lombok.*;
import jakarta.persistence.*;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name="taxrate")
public class Taxrate {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long taxrateId;

    @Column(name="tax_name")
    private String taxName;

    @Column(name="rate")
    private Integer Rate;
}