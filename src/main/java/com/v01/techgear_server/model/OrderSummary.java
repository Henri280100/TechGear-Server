package com.v01.techgear_server.model;

import lombok.*;

import java.math.BigDecimal;

import jakarta.persistence.*;


@Data
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="order_summary")
public class OrderSummary {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer orderId;
    
    @Column(name="subtotal")
    private BigDecimal subtotal;
    
    
}