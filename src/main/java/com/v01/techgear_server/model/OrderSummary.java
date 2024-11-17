package com.v01.techgear_server.model;

import lombok.*;

import java.math.BigDecimal;

import jakarta.persistence.*;

@Embeddable
@Data
public class OrderSummary {
    @Column(name="subtotal")
    private BigDecimal subtotal;

    // @Column(name="discount")
    // private BigDecimal discount;

    // @Column(name="tax")
    // private BigDecimal tax;

    @Column(name="shipping_cost")
    private BigDecimal shippingCost;
}