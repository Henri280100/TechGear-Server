package com.v01.techgear_server.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.v01.techgear_server.payment.model.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
	Payment findByPaymentId(Long paymentId);
}