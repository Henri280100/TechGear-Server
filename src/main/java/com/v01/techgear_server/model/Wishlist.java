package com.v01.techgear_server.model;

import java.util.*;
import lombok.*;
import jakarta.persistence.*;

import java.io.Serializable;
import java.time.*;

@Data
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "wishlist")
public class Wishlist implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long wishlistId;

    @Column(name="wishlist_description")
    private String wishlistDescription;

    @Column(name="created_date")
    private LocalDateTime createdDate;

    @Column(name="last_updated_date")
    private LocalDateTime lastUpdatedDate;

    @Column(name="total_value")
    private double totalValue;

    @Column(name="priority")
    private Integer priority;

    @Column(name="wishlist_image")
    @JoinColumn(name="image_id")
    private Image wishlistImage;

    @Column(name="notification")
    private boolean notifySale;

    @OneToMany(mappedBy = "wishlist", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<WishlistItems> items;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="account_details_id")
    private AccountDetails accountDetails;
}
