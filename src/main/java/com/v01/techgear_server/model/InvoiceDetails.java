package com.v01.techgear_server.model;

import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;

import jakarta.persistence.*;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name="invoice_details")
public class InvoiceDetails implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer invoiceDetailsId;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name="invoiceId")
    private Invoice invoice;

    @ManyToOne(cascade = CascadeType.ALL, fetch=FetchType.LAZY)
    @JoinColumn(name="productId")
    private Product product;

    @ManyToOne(cascade = CascadeType.ALL, fetch=FetchType.LAZY)
    @JoinColumn(name="discountId")
    private Discount discount;

    @Column(name="lineTotal", columnDefinition = "DECIMAL(10,2)")
    private BigDecimal lineTotal;
}
