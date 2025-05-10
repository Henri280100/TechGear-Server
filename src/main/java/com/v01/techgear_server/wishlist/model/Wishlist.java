package com.v01.techgear_server.wishlist.model;

import com.v01.techgear_server.shared.model.Image;
import com.v01.techgear_server.user.model.AccountDetails;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@ToString
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

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="image_id")
    private Image wishlistImage;

    @Column(name="notification")
    private boolean notifySale;

    @OneToMany(mappedBy = "wishlist", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<WishlistItems> items;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="account_details_id")
    private AccountDetails accountDetails;

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
        Wishlist wishlist = (Wishlist) o;
        return getWishlistId() != null && Objects.equals(getWishlistId(), wishlist.getWishlistId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer()
                                                                       .getPersistentClass()
                                                                       .hashCode() : getClass().hashCode();
    }
}
