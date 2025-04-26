package com.v01.techgear_server.product.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.v01.techgear_server.enums.ProductStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@ToString
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "product_detail")
public class ProductDetail implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "warranty")
    private String warranty;

    @Column(name = "voucherCode")
    private String voucherCode;

    @Column(name = "description")
    private String productDetailsDesc;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "final_price")
    private BigDecimal finalPrice;

    @Column(name = "colors")
    private String colors;

    @Column(name = "hype")
    private String hype;

    @Column(name = "title")
    private String title;

    @Column(name = "detail_image_url")
    private String detailImageUrl;

    @Column(name = "release_date")
    private LocalDateTime releaseDate;

    @Column(name = "day_left")
    private String dayLeft;

    @Column(name = "detail_video_url")
    private String detailVideoUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "product_status")
    private ProductStatus productStatus;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "product_id")
    private Product product;

    @OneToMany(mappedBy = "productDetail", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @ToString.Exclude
    private List<ProductSpecification> specifications;

    @Override
    public final boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null)
            return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy
                ? ((HibernateProxy) o).getHibernateLazyInitializer()
                .getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy
                ? ((HibernateProxy) this).getHibernateLazyInitializer()
                .getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass)
            return false;
        ProductDetail that = (ProductDetail) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer()
                .getPersistentClass()
                .hashCode() : getClass().hashCode();
    }
}
