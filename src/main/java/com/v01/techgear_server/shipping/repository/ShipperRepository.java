package com.v01.techgear_server.shipping.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.v01.techgear_server.shipping.model.Shipper;

public interface ShipperRepository extends JpaRepository<Shipper, Long>{

    
}