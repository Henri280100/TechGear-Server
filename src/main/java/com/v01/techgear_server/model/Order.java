package com.v01.techgear_server.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import com.v01.techgear_server.enums.OrderStatus;

import lombok.*;

import jakarta.persistence.*;

@Data
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "order_summary")
public class Order implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @Column(nullable = false)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @Column(nullable = false)
    private LocalDateTime orderDate;

    @OneToMany(mappedBy = "order")
    private List<OrderItems> orderItems;

    @OneToOne
    @JoinColumn(name = "payment_id")
    private Payment payment;

    @OneToOne
    @JoinColumn(name = "shipping_address_id")
    private ShippingDetails shippingDetail;

    @ManyToOne
    @JoinColumn(name = "shipper_id")
    private Shipper shipper;

    @OneToOne(mappedBy = "order")
    private Invoice invoice;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private AccountDetails accountDetails;
}