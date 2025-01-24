package com.v01.techgear_server.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.v01.techgear_server.enums.UserGenders;
import com.v01.techgear_server.enums.UserTypes;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@ToString
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "account_details")
public class AccountDetails implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long accountDetailsId;

	@Column(name = "first_name")
	private String firstName;

	@Column(name = "last_name")
	private String lastName;

	@OneToMany(mappedBy = "accountDetails", cascade = CascadeType.ALL, orphanRemoval = true)
	@ToString.Exclude
	private List<UserPhoneNo> phoneNumbers;

	@OneToMany(mappedBy = "accountDetails", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference
	@ToString.Exclude
	private List<UserAddress> addresses;

	@OneToOne
	@JoinColumn(name = "image_id")
	private Image userAvatar;

	@Column(name = "is_email_verified")
	private boolean isEmailVerified;

	@Column(name = "is_phone_number_verified")
	private boolean isPhoneNumberVerified;

	@Column(name = "account_creation_timestamp")
	private LocalDateTime registrationDate;

	@Column(name = "account_updated_timestamp")
	private LocalDateTime accountUpdatedTime;

	@Column(name = "last_login_timestamp")
	private LocalDateTime lastLoginTime;

	@Column(name = "last_updated_timestamp")
	private LocalDateTime lastUpdatedTimestamp;

	@Column(name = "userTypes")
	@Enumerated(EnumType.STRING)
	private UserTypes userTypes;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "userId")
	@ToString.Exclude
	private User users;

	@OneToMany(mappedBy = "accountDetails")
	@ToString.Exclude
	private List<Wishlist> wishlists;

	@OneToMany(mappedBy = "accountDetails", cascade = CascadeType.ALL)
	@ToString.Exclude
	private List<Order> order;

	@OneToMany(mappedBy = "accountDetails")
	@ToString.Exclude
	private List<Invoice> invoices;

	@OneToMany(mappedBy = "accountDetails", cascade = CascadeType.ALL, orphanRemoval = true)
	@ToString.Exclude
	private List<PaymentMethod> paymentMethods;

	@Column(name = "total_reviews")
	private int totalReviews;

	@Column(name = "account_age")
	private Long accountAgeDays;

	@Enumerated(EnumType.STRING)
	private UserGenders genders;

	@Column(name = "date_of_birth")
	private LocalDateTime dateOfBirth;

	@OneToMany(mappedBy = "accountDetails", cascade = CascadeType.ALL)
	@ToString.Exclude
	private List<ProductRating> productRatings;

	@OneToMany(mappedBy = "accountDetail", cascade = CascadeType.ALL)
	@ToString.Exclude
	private List<ShipperRating> shipperRatings;

	@OneToMany(mappedBy = "accountDetails", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@ToString.Exclude
	private List<OrderSummary> orderSummaries;

	@PrePersist
	public void onCreate() {
		registrationDate = LocalDateTime.now();
		accountUpdatedTime = LocalDateTime.now();
		lastLoginTime = LocalDateTime.now();
		lastUpdatedTimestamp = LocalDateTime.now();
	}

	@PreUpdate
	public void onUpdate() {
		accountUpdatedTime = LocalDateTime.now();
		lastUpdatedTimestamp = LocalDateTime.now();
	}

	// Add Method for Account Age Calculation
	public Long calculateAccountAge() {
		return ChronoUnit.DAYS.between(registrationDate, LocalDateTime.now());
	}

	// Business logic methods
	public void addPaymentMethod(PaymentMethod paymentMethod) {
		if (paymentMethods == null) {
			paymentMethods = new ArrayList<>();
		}
		paymentMethods.add(paymentMethod);
		paymentMethod.setAccountDetails(this);
	}

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
		AccountDetails that = (AccountDetails) o;
		return getAccountDetailsId() != null && Objects.equals(getAccountDetailsId(), that.getAccountDetailsId());
	}

	@Override
	public final int hashCode() {
		return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer()
		                                                               .getPersistentClass()
		                                                               .hashCode() : getClass().hashCode();
	}
}
