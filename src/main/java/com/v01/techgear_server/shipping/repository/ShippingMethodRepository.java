package com.v01.techgear_server.shipping.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.v01.techgear_server.shipping.model.ShippingMethod;

@Repository
public interface ShippingMethodRepository extends JpaRepository<ShippingMethod, Integer> {

}