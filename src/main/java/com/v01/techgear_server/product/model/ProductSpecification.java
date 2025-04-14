package com.v01.techgear_server.product.model;

import com.v01.techgear_server.common.model.Image;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@ToString
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "specification")
public class ProductSpecification implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long specId;

	@Column(name = "spec_name")
	private String specName;

	@Column(name = "spec_value")

	private String specValue;
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "image_id")
	private Image specImage;

	@Column(name = "icon")
	private String icon;

	@ManyToOne
	@JoinColumn(name = "product_detail_id")
	private ProductDetail productDetail;

	@Override
	public final boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null)
			return false;
		Class<?> oEffectiveClass = o instanceof HibernateProxy
				? ((HibernateProxy) o).getHibernateLazyInitializer()
				                      .getPersistentClass()
				: o.getClass();
		Class<?> thisEffectiveClass = this instanceof HibernateProxy
				? ((HibernateProxy) this).getHibernateLazyInitializer()
				                         .getPersistentClass()
				: this.getClass();
		if (thisEffectiveClass != oEffectiveClass)
			return false;
		ProductSpecification that = (ProductSpecification) o;
		return getSpecId() != null && Objects.equals(getSpecId(), that.getSpecId());
	}

	@Override
	public final int hashCode() {
		return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer()
		                                                               .getPersistentClass()
		                                                               .hashCode() : getClass().hashCode();
	}
}
