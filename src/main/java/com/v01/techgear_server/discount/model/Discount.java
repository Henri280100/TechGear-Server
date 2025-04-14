package com.v01.techgear_server.discount.model;

import com.v01.techgear_server.enums.DiscountType;
import com.v01.techgear_server.invoice.model.InvoiceDetails;

import com.v01.techgear_server.product.model.Product;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

@ToString
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "discounts")
public class Discount implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long discountId;

    @Column(name = "discount_percentage")
    private Double discountPercentage;

    @Column(name = "discount_code")
    private String discountCode;

    @Column(name = "discount_name")
    private String discountName;

    @Column(name = "is_discount_active")
    private String isDiscountActive;

    @Column(name = "discount_type")
    @Enumerated(EnumType.STRING)
    private DiscountType discountType;

    @Column(name = "discount_description")
    private String discountDescription;

    @Column(name = "discount_status")
    private String discountStatus;

    @Column(name = "discount_limit")
    private Integer discountLimit;

    @Column(name = "start_date")
    private String startDate;

    @Column(name = "expiry_date")
    private String expiryDate;

    @JoinTable(name = "product_discount", joinColumns = @JoinColumn(name = "discount_id"), inverseJoinColumns = @JoinColumn(name = "product_id"))
    private List<Product> products; // Relationship with Product

    @OneToMany(mappedBy = "discount", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<InvoiceDetails> invoiceDetails;

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
        Discount discount = (Discount) o;
        return getDiscountId() != null && Objects.equals(getDiscountId(), discount.getDiscountId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer()
                                                                       .getPersistentClass()
                                                                       .hashCode() : getClass().hashCode();
    }
}