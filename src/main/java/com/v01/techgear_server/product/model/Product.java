package com.v01.techgear_server.product.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.v01.techgear_server.discount.model.Discount;
import com.v01.techgear_server.enums.ProductAvailability;
import com.v01.techgear_server.order.model.OrderItem;
import com.v01.techgear_server.wishlist.model.WishlistItems;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@ToString
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "product")
public class Product implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    @Column(name = "name", unique = true)
    private String name;

    @Column(name = "product_description")
    private String productDescription;

    @Enumerated(EnumType.STRING)
    @Column(name = "product_availability")
    private ProductAvailability availability;

    @Column(name = "stock_level")
    private int stockLevel;

    @Column(name = "slug", unique = true)
    private String slug;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "min_price")
    private BigDecimal minPrice;

    @Column(name = "max_price")
    private BigDecimal maxPrice;

    @Column(name = "brand")
    private String brand;

    @Column(name = "features")
    private String features;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "category_id")
    private ProductCategory category;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "product_tags", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "tag")
    private List<String> tags = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "product")
    @ToString.Exclude
    @JsonIgnore
    private List<ProductDetail> productDetails;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "product")
    @ToString.Exclude
    private List<ProductRating> productRatings;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<OrderItem> orderItems;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<WishlistItems> wishlistItems;

    @ManyToMany
    @JoinTable(
            name = "product_discount",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "discount_id")
    )
    @ToString.Exclude
    private List<Discount> discounts;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy
                ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass()
                : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy
                ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass()
                : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Product product = (Product) o;
        return getProductId() != null && Objects.equals(getProductId(), product.getProductId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer()
                .getPersistentClass()
                .hashCode() : getClass().hashCode();
    }
}
