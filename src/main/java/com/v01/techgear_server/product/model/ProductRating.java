package com.v01.techgear_server.product.model;

import com.v01.techgear_server.common.model.Image;
import com.v01.techgear_server.user.model.AccountDetails;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@ToString
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "product_rating")
public class ProductRating implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long productRatingId;

    @Column(name = "comments")
    private String comments;

    @Column(name = "rating")
    private double rating;

    @Column(name = "rating_date")
    private LocalDateTime ratingDate;


    @Column(name = "average_rating")
    private double averageRating;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "image_id")
    private Image reviewImage;

    @ManyToOne
    @JoinColumn(name = "accountDetailsId")
    private AccountDetails accountDetails;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "product_id")
    private Product product;

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
        ProductRating that = (ProductRating) o;
        return getProductRatingId() != null && Objects.equals(getProductRatingId(), that.getProductRatingId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer()
                                                                       .getPersistentClass()
                                                                       .hashCode() : getClass().hashCode();
    }
}
