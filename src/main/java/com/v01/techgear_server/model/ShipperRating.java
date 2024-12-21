package com.v01.techgear_server.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Data
@Entity
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
    private LocalDateTime ratingDateTime;

    @ManyToOne
    @JoinColumn(name = "shipper_id")
    private transient Shipper shipper;

    @ManyToOne
    @JoinColumn(name = "accountDetailsId")
    private AccountDetails accountDetails;
}
