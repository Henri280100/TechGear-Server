package com.v01.techgear_server.shipping.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.v01.techgear_server.shipping.model.ShipperRating;

@Repository
public interface ShipperRatingRepository extends JpaRepository<ShipperRating, Long> {


}