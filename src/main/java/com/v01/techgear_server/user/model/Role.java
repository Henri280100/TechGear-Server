package com.v01.techgear_server.user.model;

import com.v01.techgear_server.enums.Roles;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Role implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Enumerated(EnumType.STRING)
	@Column(name="role_type", unique = true, nullable = false)
	private Roles roleType;

	@ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@JoinTable(name = "role_permissions",
	           joinColumns = @JoinColumn(name = "role_id"),
	           inverseJoinColumns = @JoinColumn(name = "permission_id"))
	private Set<Permission> permissions = new HashSet<>();

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
		Role role = (Role) o;
		return getId() != null && Objects.equals(getId(), role.getId());
	}

	@Override
	public final int hashCode() {
		return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer()
		                                                               .getPersistentClass()
		                                                               .hashCode() : getClass().hashCode();
	}
}
