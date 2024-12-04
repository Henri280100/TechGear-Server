package com.v01.techgear_server.model;

import jakarta.persistence.*;
import lombok.*;


@Data
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name="wishlist_items")
public class WishlistItems {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="product_name", nullable = false, unique = true)
    private String productName; // Name of the product

    @Column(name="price", nullable = false, unique = true)
    private Double price; // Price of the product

    @Column(name="image_url", nullable = false, unique = true)
    private Image imageUrl; // Image URL of the product

    @Column(name="notes")
    private String notes; // Any additional notes about the item

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "wishlist_id", nullable = false)
    private Wishlist wishlist;

}