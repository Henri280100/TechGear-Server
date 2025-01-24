package com.v01.techgear_server.model;

import com.v01.techgear_server.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.proxy.HibernateProxy;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@ToString
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "order_summary")
public class OrderSummary implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long orderSummaryId;

	@Column(name = "sub_total")
	private Double subTotal;

	@Column(name = "shipping_cost")
	private Double shippingCost;

	@Column(name = "notes")
	private String notes;

	@Column(name = "currency")
	private String currency;

	@Column(name = "totalAmount")
	private Double totalAmount;

	@Column(name = "orderDate")
	private LocalDateTime orderDate;

	@Enumerated(EnumType.STRING)
	private OrderStatus orderStatus;

	@Column(name = "createdAt", updatable = false)
	@CreationTimestamp
	private LocalDateTime createdAt;

	@Column(name = "updatedAt")
	@UpdateTimestamp
	private LocalDateTime updatedAt;

	@ManyToOne
	@JoinColumn(name = "accountDetailsId", nullable = false)
	private AccountDetails accountDetails;

	@OneToMany(mappedBy = "orderSummary", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@ToString.Exclude
	private List<OrderItems> orderItems;

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
		OrderSummary that = (OrderSummary) o;
		return getOrderSummaryId() != null && Objects.equals(getOrderSummaryId(), that.getOrderSummaryId());
	}

	@Override
	public final int hashCode() {
		return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer()
		                                                               .getPersistentClass()
		                                                               .hashCode() : getClass().hashCode();
	}
}
