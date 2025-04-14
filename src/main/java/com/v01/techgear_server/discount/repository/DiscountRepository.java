package com.v01.techgear_server.discount.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.v01.techgear_server.discount.model.Discount;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, Long> {

    
}