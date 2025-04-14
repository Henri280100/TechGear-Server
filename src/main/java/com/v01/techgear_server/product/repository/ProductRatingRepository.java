package com.v01.techgear_server.product.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.v01.techgear_server.product.model.ProductRating;

@Repository
public interface ProductRatingRepository extends JpaRepository<ProductRating, Long> {

    @Modifying
    @Query("SELECT pr FROM ProductRating pr WHERE pr.product.productId = :productId")
    void findAllByProductId(Long productId);


    @Modifying
    @Query("SELECT pr FROM ProductRating pr WHERE pr.product.productId = :productId AND pr.productRatingId = :reviewId")
    Optional<ProductRating> findByProductIdAndProductRatingId(Long productId, Long reviewId);
}