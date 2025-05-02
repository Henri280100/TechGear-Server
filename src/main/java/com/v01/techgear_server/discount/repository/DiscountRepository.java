package com.v01.techgear_server.discount.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.v01.techgear_server.discount.model.Discount;

import java.util.List;
import java.util.Optional;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, Long> {
    @Query("SELECT d FROM Discount d WHERE d.discountCode = :code")
    List<Discount> findByDiscountCode(@Param("code") String code);


    @Query("SELECT d FROM Discount d JOIN d.products p WHERE p.productId = :productId")
    Optional<Discount> findDiscountFromProducts(@Param("productId") Long productId);


}