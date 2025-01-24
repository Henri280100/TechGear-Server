package com.v01.techgear_server.repo.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.v01.techgear_server.model.OrderItems;

@Repository
public interface OrderItemsRepository extends JpaRepository<OrderItems, Integer> {

}