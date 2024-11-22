package com.v01.techgear_server.model;

import lombok.*;
import jakarta.persistence.*;
import java.util.*;

@Data
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="shipping_method")
public class ShippingMethod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer shippingMethodId;

    @Column(name = "shipping_method_name")
    private String shippingMethodName;

    @Column(name= "shipping_method_description")
    private String shippingMethodDescription;

    @Column(name = "shipping_method_cost")
    private Double shippingMethodCost;

    @Column(name="delivery_time")
    private String deliveryTime;

    @OneToMany(mappedBy = "shippingMethod", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ShippingDetails> shippingDetails;
}