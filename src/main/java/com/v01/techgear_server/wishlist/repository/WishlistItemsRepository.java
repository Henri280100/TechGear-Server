package com.v01.techgear_server.wishlist.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.v01.techgear_server.wishlist.model.WishlistItems;

@Repository
public interface WishlistItemsRepository extends JpaRepository<WishlistItems, Long> {

    
}