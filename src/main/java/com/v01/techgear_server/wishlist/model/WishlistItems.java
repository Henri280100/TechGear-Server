package com.v01.techgear_server.wishlist.model;

import com.v01.techgear_server.common.model.Image;
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
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "wishlist_items")
public class WishlistItems implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "product_name", nullable = false, unique = true)
	private String productName; // Name of the product

	@Column(name = "price", nullable = false, unique = true)
	private Double price; // Price of the product

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "image_id")
	@ToString.Exclude
	private Image wishListItemsImage; // Image URL of the product

	@Column(name = "notes")
	private String notes; // Any additional notes about the item

	@ManyToOne
	@JoinColumn(name = "product_id", nullable = false)
	private Product product;

	@ManyToOne
	@JoinColumn(name = "wishlist_id", nullable = false)
	private Wishlist wishlist;

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
		WishlistItems that = (WishlistItems) o;
		return getId() != null && Objects.equals(getId(), that.getId());
	}

	@Override
	public final int hashCode() {
		return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer()
		                                                               .getPersistentClass()
		                                                               .hashCode() : getClass().hashCode();
	}
}