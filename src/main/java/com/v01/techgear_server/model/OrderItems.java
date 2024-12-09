package com.v01.techgear_server.model;

import lombok.*;

import java.io.Serializable;

import com.v01.techgear_server.enums.OrderItemStatus;

import jakarta.persistence.*;

@Data
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="order_items")
public class OrderItems implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer orderItemsId;

    @Column(name="quantity")
    private Integer quantity;
    
    @Column(name="price")
    private Double price;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @Enumerated(EnumType.STRING)
    private OrderItemStatus orderItemStatus;
}