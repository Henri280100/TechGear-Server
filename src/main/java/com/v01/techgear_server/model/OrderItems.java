package com.v01.techgear_server.model;

import lombok.*;

import java.math.BigDecimal;

import com.v01.techgear_server.enums.OrderItemStatus;

import jakarta.persistence.*;

@Data
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="order_items")
public class OrderItems {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer OrderItemsId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private OrderHistory orderHistory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name="quantity")
    private Integer quantity;

    @Column(name="unit_price")
    private BigDecimal unitPrice;

    @Enumerated(EnumType.STRING)
    private OrderItemStatus orderItemStatus;
}