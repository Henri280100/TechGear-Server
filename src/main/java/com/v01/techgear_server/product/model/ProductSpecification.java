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

	@Column(name="image_url")
	private String imageUrl;

	@ManyToOne
	@JoinColumn(name = "product_detail_id")
	private ProductDetail productDetail;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ProductSpecification that = (ProductSpecification) o;
		return Objects.equals(specId, that.specId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(specId);
	}
}
