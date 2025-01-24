package com.v01.techgear_server.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDateTime;
import java.util.Objects;

@ToString
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "shipping_details")
public class ShippingDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long shippingDetailsId;

    @Column(name = "shipping_username")
    private String username;

    @Column(name = "shipping_address_line1")
    private String addressLine1;

    @Column(name = "shipping_address_line2")
    private String addressLine2;

    @Column(name = "shipping_city")
    private String city;

    @Column(name = "shipping_state")
    private String state;

    @Column(name = "shipping_postal_code")
    private String postalCode;

    @Column(name = "shipping_country")
    private String country;

    @Column(name = "shipping_phone")
    private String phoneNumber;

    @Column(name = "shipping_email")
    private String email;

    @Column(name = "shipping_date")
    private LocalDateTime shippingDate;

    @Column(name = "estimated_delivery_date")
    private LocalDateTime estimatedDeliveryDate;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "shippingMethodId")
    @ToString.Exclude
    private ShippingMethod shippingMethod;

    @OneToOne(mappedBy = "shippingDetail")
    private Order order;

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
        ShippingDetails that = (ShippingDetails) o;
        return getShippingDetailsId() != null && Objects.equals(getShippingDetailsId(),
                                                                that.getShippingDetailsId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer()
                                                                       .getPersistentClass()
                                                                       .hashCode() : getClass().hashCode();
    }
}