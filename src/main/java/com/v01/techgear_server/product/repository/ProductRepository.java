package com.v01.techgear_server.product.repository;

import java.util.*;

import com.v01.techgear_server.product.dto.ProductDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.v01.techgear_server.product.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    @Query("SELECT new com.v01.techgear_server.product.dto.ProductDTO(" +
            "p.productId, p.name, p.productDescription, p.price, p.minPrice, p.maxPrice, " +
            "LOWER(REPLACE(CAST(p.availability AS string), '_', ' ')), p.stockLevel, p.brand, p.image.imageUrl, p.features, " +
            "LOWER(REPLACE(CAST(p.category AS string), '_', ' '))) " +
            "FROM Product p")
    Page<ProductDTO> findAllForIndexing(Pageable pageable);


    @Modifying
    @Query("SELECT p FROM Product p WHERE p.name = :name")
    Optional<Product> findProductByName(String name);

    @Modifying
    @Query("SELECT p FROM Product p WHERE p.category = :category")
    Optional<Product> findProductByCategory(String category);

    @Modifying
    @Query("SELECT p FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice")
    List<Product> findProductsByPriceRange(Double minPrice, Double maxPrice);

    @Modifying
    @Query("SELECT p FROM Product p WHERE p.brand = :brand")
    Optional<Product> findProductByBrand(String brand);

    @Modifying
    @Query("SELECT p FROM Product p WHERE p.productId = :id")
    Optional<Product> findProductById(Long id);

    @Query("SELECT p FROm Product p WHERE p.availability = :availability")
    List<Product> findProductByAvailability(String availability);

    @Query("SELECT p FROM Product p WHERE p.stockLevel > 0")
    List<Product> findProductByStockLevel(Integer stockLevel);

    @Query("SELECT p FROM Product p WHERE p.brand = :brand AND p.category = :category AND p.price BETWEEN :minPrice AND :maxPrice")
    List<Product> findProductByBrandAndCategory(String brand, String category, Double minPrice, Double maxPrice);

}
