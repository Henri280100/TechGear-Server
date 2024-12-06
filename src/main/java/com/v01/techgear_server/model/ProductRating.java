package com.v01.techgear_server.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "product_rating")
public class ProductRating implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long productRatingId;

    @Column(name = "comments")
    private String comments;

    @Column(name = "rating")
    private double rating;

    @Column(name = "rating_date")
    private LocalDateTime ratingDate;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "image_id")
    private Image reviewImage;

    @ManyToOne
    @JoinColumn(name = "accountDetailsId")
    private AccountDetails accountDetails;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "product_id")
    private Product product;
}
