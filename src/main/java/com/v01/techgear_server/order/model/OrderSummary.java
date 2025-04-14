package com.v01.techgear_server.order.model;

import com.v01.techgear_server.user.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@ToString
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "order_summary")
public class OrderSummary implements Serializable {



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderSummaryId;

    @Column(name = "order_number")
    private String orderNumber;

    @Column(name = "order_date")
    private LocalDateTime orderDate;

    @Column(name = "status")
    private String status;

    @Column(name = "total_price")
    private Double totalPrice;


    @OneToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;


}
