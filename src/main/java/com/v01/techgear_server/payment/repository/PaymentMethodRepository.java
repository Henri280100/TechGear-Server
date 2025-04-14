package com.v01.techgear_server.payment.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.v01.techgear_server.payment.model.PaymentMethod;

@Repository
public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, UUID> {

    
}