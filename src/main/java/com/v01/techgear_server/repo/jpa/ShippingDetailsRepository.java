package com.v01.techgear_server.repo.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.v01.techgear_server.model.ShippingDetails;

@Repository
public interface ShippingDetailsRepository extends JpaRepository<ShippingDetails, Long> {
    
}
