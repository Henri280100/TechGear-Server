package com.v01.techgear_server.invoice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.v01.techgear_server.invoice.model.InvoiceDetails;

@Repository
public interface InvoiceDetailsRepository extends JpaRepository<InvoiceDetails, Integer> {

    
}