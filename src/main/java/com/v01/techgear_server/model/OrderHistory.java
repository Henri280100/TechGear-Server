package com.v01.techgear_server.model;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.v01.techgear_server.enums.OrderStatus;
import com.v01.techgear_server.enums.PaymentStatus;

import jakarta.persistence.*;

@Data
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "order_history")
public class OrderHistory {
     @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderHistoryId;

    @Column(name = "order_number", unique = true)
    private String orderNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User user;

    @OneToMany(
        mappedBy = "orderHistory", 
        cascade = CascadeType.ALL, 
        fetch = FetchType.LAZY
    )
    private List<OrderItems> orderItems = new ArrayList<>();

    @Embedded
    private OrderSummary orderSummary;
    
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "shippingDetailsId")
    private ShippingDetails shippingDetails;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @Column(name = "total_amount")
    private BigDecimal totalAmount;

    @Column(name = "total_items")
    private Integer totalItems;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @Column(name = "payment_method")
    private String paymentMethod;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Tracking and Shipping
    @Column(name = "tracking_number")
    private String trackingNumber;

    @Column(name = "shipping_carrier")
    private String shippingCarrier;

    // Calculated Fields
    @Transient
    private BigDecimal discountAmount;

    @Transient
    private BigDecimal taxAmount;

}