package com.v01.techgear_server.product.repository;

import com.v01.techgear_server.product.dto.ProductDTO;
import com.v01.techgear_server.product.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    @Query("SELECT new com.v01.techgear_server.product.dto.ProductDTO(" +
            "p.productId, p.name, COALESCE(p.productDescription, ''), " +
            "(SELECT pd.finalPrice FROM ProductDetail pd WHERE pd.product = p ORDER BY pd.releaseDate ASC LIMIT 1), " +
            "p.minPrice, p.maxPrice, " +
            "LOWER(REPLACE(p.availability, '_', ' ')), p.stockLevel, p.brand, p.imageUrl, p.features, " +
            "p.category.categoryName) " +
            "FROM Product p")
    Page<ProductDTO> findAllForIndexing(Pageable pageable);



    @Query("SELECT p FROM Product p WHERE LOWER(p.category.categoryName) IN :category")
    List<Product> findByCategoryInIgnoreCase(@Param("category") List<String> category);

    @Query("SELECT p FROM Product p WHERE p.name = :name")
    Optional<Product> findProductByName(String name);

    @Query("SELECT p FROM Product p WHERE p.category.categoryName = :category")
    Optional<Product> findProductByCategory(String category);

    @Query("SELECT DISTINCT p FROM Product p JOIN p.productDetails pd " +
            "WHERE pd.finalPrice BETWEEN :minPrice AND :maxPrice")
    List<Product> findProductsByPriceRange(Double minPrice, Double maxPrice);

    @Query("SELECT p FROM Product p WHERE p.brand = :brand")
    Optional<Product> findProductByBrand(String brand);

    @Query("SELECT p FROM Product p WHERE p.productId = :id")
    Optional<Product> findProductById(Long id);

    @Query("SELECT p FROm Product p WHERE p.availability = :availability")
    List<Product> findProductByAvailability(String availability);

    @Query("SELECT p FROM Product p WHERE p.stockLevel > 0")
    List<Product> findProductByStockLevel(Integer stockLevel);

    @Query("SELECT p FROM Product p WHERE p.brand = :brand AND p.category = :category")
    List<Product> findProductByBrandAndCategory(String brand, String category);

}
