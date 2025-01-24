package com.v01.techgear_server.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "shipper_rating")
public class ShipperRating implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "comments")
	private String comments;

	@Column(name = "rating")
	private double rating;

	@Column(name = "rating_date")
	private LocalDateTime ratingDate;

	@ManyToOne
	@JoinColumn(name = "shipper_id")
	private Shipper shipper;

	@ManyToOne
	@JoinColumn(name = "accountDetailsId")
	private AccountDetails accountDetail;
}
