package com.v01.techgear_server.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.util.List;
import java.util.Objects;

@ToString
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "shipping_method")
public class ShippingMethod {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer shippingMethodId;

	@Column(name = "shipping_method_name")
	private String shippingMethodName;

	@Column(name = "shipping_method_description")
	private String shippingMethodDescription;

	@Column(name = "shipping_method_cost")
	private Double shippingMethodCost;

	@Column(name = "delivery_time")
	private String deliveryTime;

	@OneToMany(mappedBy = "shippingMethod", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@ToString.Exclude
	private List<ShippingDetails> shippingDetails;

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
		ShippingMethod that = (ShippingMethod) o;
		return getShippingMethodId() != null && Objects.equals(getShippingMethodId(), that.getShippingMethodId());
	}

	@Override
	public final int hashCode() {
		return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer()
		                                                               .getPersistentClass()
		                                                               .hashCode() : getClass().hashCode();
	}
}