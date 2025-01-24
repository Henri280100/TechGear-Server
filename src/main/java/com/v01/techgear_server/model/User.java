package com.v01.techgear_server.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.v01.techgear_server.dto.UserCreateUpdateDTO;
import com.v01.techgear_server.enums.AuthProvider;
import com.v01.techgear_server.enums.UserStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ToString
@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User implements UserDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long userId;


	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "user_connections",
	           joinColumns = @JoinColumn(name = "user_id"),
	           inverseJoinColumns = @JoinColumn(name = "connection_id"))
	@ToString.Exclude
	private Set<User> connections = new HashSet<>();

	@Column(name = "username")
	private String username;

	@Column(name = "password")
	private String password;

	@Column(name = "email", unique = true)
	private String email;

	@Enumerated(EnumType.STRING)
	private AuthProvider provider; // for storing data in gg or fb

	@OneToOne(mappedBy = "users", cascade = CascadeType.ALL)
	private AccountDetails accountDetails;

	@OneToMany(mappedBy = "users", cascade = CascadeType.ALL)
	@JsonIgnore
	@ToString.Exclude
	private List<ConfirmationTokens> confirmationTokens;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "userId"),
	           inverseJoinColumns = @JoinColumn(name = "role_id"))
	@ToString.Exclude
	private Set<Role> roles = new HashSet<>();

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "user_permissions", joinColumns = @JoinColumn(name = "userId"),
	           inverseJoinColumns = @JoinColumn(name = "permission_id"))
	@ToString.Exclude
	private Set<Permission> permissions = new HashSet<>();

	@Column(name = "active")
	private boolean active;

	@Column(name = "login_attempts")
	private int loginAttempts;

	@Enumerated(EnumType.STRING)
	@Column(name = "user_status")
	private UserStatus userStatus;


	@Column(name = "last_active_before")
	public LocalDateTime lastLoginActiveBefore;

	@Column(name = "account_locked")
	private boolean accountLocked;

	@Column(name = "account_expired")
	private boolean accountExpired;

	public User(UserCreateUpdateDTO userCreateUpdateDTO) {
		this.username = userCreateUpdateDTO.getUsername();
		this.password = userCreateUpdateDTO.getPassword();
		this.email = userCreateUpdateDTO.getEmail();
		this.active = userCreateUpdateDTO.isActive();
		this.userStatus = userCreateUpdateDTO.getUserStatus();
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return this.roles.stream()
		                 .flatMap(role -> Stream.concat(
				                 Stream.of(new SimpleGrantedAuthority(role.getRoleType()
				                                                          .name())),
				                 role.getRoleType()
				                     .getPermissions()
				                     .stream()
				                     .map(permission -> new SimpleGrantedAuthority(permission.name()))))
		                 .collect(Collectors.toSet());
	}


	@CreationTimestamp
	@Column(updatable = false, name = "createdDate")
	private LocalDateTime createdDate;

	@UpdateTimestamp
	@Column(name = "updatedDate")
	private LocalDateTime updatedDate;

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public boolean isAccountNonExpired() {
		return !accountExpired;
	}

	@Override
	public boolean isAccountNonLocked() {
		return !accountLocked;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return UserDetails.super.isCredentialsNonExpired();
	}

	@Override
	public boolean isEnabled() {
		return active &&
				confirmationTokens != null &&
				confirmationTokens
						.stream()
						.anyMatch(token -> token.getConfirmedAt() != null);
	}

	public int getLoginAttempts() {
		return loginAttempts;
	}

	public void incrementLoginAttempts() {
		this.loginAttempts++;
	}

	public void resetLoginAttempts() {
		this.loginAttempts = 0;
	}

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
		User user = (User) o;
		return getUserId() != null && Objects.equals(getUserId(), user.getUserId());
	}

	@Override
	public final int hashCode() {
		return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer()
		                                                               .getPersistentClass()
		                                                               .hashCode() : getClass().hashCode();
	}
}
