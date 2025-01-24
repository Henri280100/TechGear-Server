package com.v01.techgear_server.repo.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.v01.techgear_server.model.Wishlist;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long> {

    
}