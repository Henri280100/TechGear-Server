package com.v01.techgear_server.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@ToString
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "shipper")
public class Shipper {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long shipperId;

	@Column(name = "shipper_name")
	private String shipperName;

	@OneToMany(mappedBy = "shipper", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	@ToString.Exclude
	private List<Order> orders = new ArrayList<>();

	@OneToMany(mappedBy = "shipper", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	@ToString.Exclude
	private List<ShipperRating> shipperRatings = new ArrayList<>();

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
		Shipper shipper = (Shipper) o;
		return getShipperId() != null && Objects.equals(getShipperId(), shipper.getShipperId());
	}

	@Override
	public final int hashCode() {
		return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer()
		                                                               .getPersistentClass()
		                                                               .hashCode() : getClass().hashCode();
	}
}