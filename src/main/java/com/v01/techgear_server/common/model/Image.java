package com.v01.techgear_server.common.model;


import com.v01.techgear_server.enums.ImageTypes;
import com.v01.techgear_server.product.model.Product;
import com.v01.techgear_server.product.model.ProductRating;
import com.v01.techgear_server.product.model.ProductSpecification;
import com.v01.techgear_server.user.model.AccountDetails;
import com.v01.techgear_server.wishlist.model.Wishlist;
import com.v01.techgear_server.wishlist.model.WishlistItems;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
public class Image implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name="image_url")
    private String imageUrl;

    @Column(name = "filename")
    private String fileName;

    @Column(name = "content_type")
    @Pattern(regexp = "^image/(jpeg|png|gif|webp|bmp)$",
            message = "Invalid image content type")
    private String contentType;

    @Column(name = "data", columnDefinition="BYTEA")
    private byte[] data; // Store image data as a byte array

    // Metadata fields
    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "width")
    private Integer width;

    @Column(name = "height")
    private Integer height;

    // Auditing fields
    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name="last_modified_at")
    private LocalDateTime lastModifiedAt;

    // Enum for an image type
    @Enumerated(EnumType.STRING)
    @Column(name = "image_type")
    private ImageTypes imageTypes;

    @OneToOne(mappedBy="userAvatar", cascade = CascadeType.ALL)
    private AccountDetails accountDetails;

    @OneToOne(mappedBy="reviewImage", cascade = CascadeType.ALL)
    private ProductRating productRating;

    @OneToOne(mappedBy="wishlistImage", cascade = CascadeType.ALL)
    private Wishlist wishlist;

    @OneToOne(mappedBy="wishListItemsImage", cascade = CascadeType.ALL)
    private WishlistItems wishlistItems;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer()
                .getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer()
                .getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Image image = (Image) o;
        return getId() != null && Objects.equals(getId(), image.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer()
                .getPersistentClass()
                .hashCode() : getClass().hashCode();
    }
}

