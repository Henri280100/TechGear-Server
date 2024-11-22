package com.v01.techgear_server.model;

import lombok.*;

import java.math.BigDecimal;

import jakarta.persistence.*;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name="invoice_details")
public class InvoiceDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer invoiceDetailsId;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name="invoice_id")
    private Invoice invoice;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name="taxrate_id")
    private Taxrate taxrate;

    @ManyToOne(cascade = CascadeType.ALL, fetch=FetchType.LAZY)
    @JoinColumn(name="product_id")
    private Product product;

    @ManyToOne(cascade = CascadeType.ALL, fetch=FetchType.LAZY)
    @JoinColumn(name="discount_id")
    private Discount discount;

    @Column(name="line_total", columnDefinition = "DECIMAL(10,2)")
    private BigDecimal lineTotal;
}
