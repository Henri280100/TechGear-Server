package com.v01.techgear_server.model;

import java.math.BigDecimal;
import java.util.*;
import lombok.*;
import jakarta.persistence.*;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "taxrate")
public class Taxrate {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long taxrateId;

    @Column(name = "tax_name")
    private String taxName;

    @Column(name = "rate", columnDefinition = "DECIMAL(5,2)")
    private BigDecimal Rate;

    @OneToMany(mappedBy = "taxrate", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<InvoiceDetails> invoiceDetails = new ArrayList<InvoiceDetails>();
}