package com.v01.techgear_server.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.v01.techgear_server.enums.Category;
import com.v01.techgear_server.enums.ProductAvailability;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "product")
public class Product implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long productId;

    @Column(name = "name", unique = true)
    private String name;

    @Column(name = "product_description", unique = true)
    private String productDescription;

    @Enumerated(EnumType.STRING)
    @Column(name = "product_availability")
    private ProductAvailability availability;

    @Column(name="stock_level")
    private int stockLevel;

    @Column(name = "slug", unique = true)
    private String slug;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "image_id")
    private Image image;

    @Column(name = "price")
    private double price;

    @Column(name = "sku")
    private String sku;

    @Enumerated(EnumType.STRING)
    private Category category;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "product")
    private ProductDetail productDetail;

    @OneToMany(mappedBy = "product")
    private List<ProductRating> productRatings = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<OrderItems> orderItems = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<WishlistItems> wishlistItems = new ArrayList<>();
}
