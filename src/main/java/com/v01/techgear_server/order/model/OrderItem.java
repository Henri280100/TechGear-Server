package com.v01.techgear_server.order.model;

import com.v01.techgear_server.enums.OrderItemStatus;
import com.v01.techgear_server.product.model.Product;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.io.Serializable;
import java.util.Objects;

@ToString
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="order_items")
public class OrderItem implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer orderItemsId;

    @Column(name="quantity")
    private Integer quantity;
    
    @Column(name="price")
    private Double price;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @Enumerated(EnumType.STRING)
    private OrderItemStatus orderItemStatus;

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
        OrderItem that = (OrderItem) o;
        return getOrderItemsId() != null && Objects.equals(getOrderItemsId(), that.getOrderItemsId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer()
                                                                       .getPersistentClass()
                                                                       .hashCode() : getClass().hashCode();
    }
}