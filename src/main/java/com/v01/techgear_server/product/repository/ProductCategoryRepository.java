package com.v01.techgear_server.product.repository;

import com.v01.techgear_server.product.model.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {
    Optional<ProductCategory> findFirstByCategoryName(String categoryName);

    List<ProductCategory> findByCategoryNameContainingIgnoreCase(String categoryName);
}
