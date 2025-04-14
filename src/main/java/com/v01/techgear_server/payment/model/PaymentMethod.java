package com.v01.techgear_server.payment.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.v01.techgear_server.enums.PaymentStatus;
import com.v01.techgear_server.enums.PaymentType;
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
@Table(name="payment_method")
public class PaymentMethod implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String stripePaymentMethodId;

    @Enumerated(EnumType.STRING)
    private PaymentType type;

    @Column(nullable = false)
    private String lastFourDigits;

    @Column(nullable = false)
    private String cardBrand;

    @Column(nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime expirationDate;

    @Column(nullable = false)
    private Boolean isDefault = false;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_details_id", nullable = false)
    @ToString.Exclude
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
        PaymentMethod that = (PaymentMethod) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer()
                                                                       .getPersistentClass()
                                                                       .hashCode() : getClass().hashCode();
    }
}