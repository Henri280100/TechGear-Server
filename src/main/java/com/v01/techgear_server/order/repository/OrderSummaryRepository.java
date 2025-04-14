package com.v01.techgear_server.order.repository;

import com.v01.techgear_server.order.model.OrderSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderSummaryRepository extends JpaRepository<OrderSummary, Long> {

}