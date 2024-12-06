package com.v01.techgear_server.model;

import java.util.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.v01.techgear_server.enums.OrderStatus;

import java.io.Serializable;
import java.time.*;
import lombok.*;
import jakarta.persistence.*;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "order_summary")
public class OrderSummary implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long orderSummaryId;

    @Column(name = "sub_total")
    private Double subTotal;

    @Column(name = "shipping_cost")
    private Double shippingCost;

    @Column(name = "notes")
    private String notes;

    @Column(name = "currency")
    private String currency;

    @Column(name = "total_amount")
    private Double totalAmount;

    @Column(name = "order_date")
    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "accountDetailsId", nullable = false)
    private AccountDetails accountDetails;

    @OneToMany(mappedBy = "orderSummary", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderItems> orderItems;

}
