package com.v01.techgear_server.product.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.v01.techgear_server.enums.Category;
import com.v01.techgear_server.product.model.Product;
import com.v01.techgear_server.product.model.ProductDetail;

@Repository
public interface ProductDetailRepository
        extends JpaRepository<ProductDetail, Long>, JpaSpecificationExecutor<ProductDetail> {
    // Find by Product
    Optional<ProductDetail> findByProduct(Product product);

    List<ProductDetail> findByProductProductId(Long productId);

    // Find by Warranty
    List<ProductDetail> findByWarranty(String warranty);

    List<ProductDetail> findByWarrantyContaining(String warrantyKeyword);

    // Find by Voucher Code
    Optional<ProductDetail> findByVoucherCode(String voucherCode);

    List<ProductDetail> findByVoucherCodeStartingWith(String prefix);


    // Complex Query Methods
    @Query("SELECT pd FROM ProductDetail pd WHERE " +
            "(:warranty IS NULL OR pd.warranty LIKE %:warranty%) AND " +
            "(:voucherCode IS NULL OR pd.voucherCode = :voucherCode)")
    List<ProductDetail> searchByWarrantyAndVoucherCode(
            @Param("warranty") String warranty,
            @Param("voucherCode") String voucherCode);

    // Media Related Queries
    @Query("SELECT pd FROM ProductDetail pd WHERE " +
            "pd.detailImageUrl IS NOT NULL OR pd.detailVideoUrl IS NOT NULL")
    List<ProductDetail> findWithMedia();

    // Specification Related Queries
    @Query("SELECT pd FROM ProductDetail pd JOIN pd.specifications ps " +
            "WHERE ps.specName = :specName")
    List<ProductDetail> findBySpecificationName(@Param("specName") String specName);

    // Count Methods
    long countByWarranty(String warranty);

    long countByVoucherCodeNotNull();

    // Delete Methods
    void deleteByProduct(Product product);

    void deleteByVoucherCode(String voucherCode);




    // Batch Update Methods
    @Modifying
    @Query("UPDATE ProductDetail pd SET pd.warranty = :newWarranty " +
            "WHERE pd.product.category = :category")
    int updateWarrantyByCategory(
            @Param("category") Category category,
            @Param("newWarranty") String newWarranty);

    // Native Query Example
    @Query(value = "SELECT * FROM product_details pd " +
            "WHERE LENGTH(pd.voucher_code) > :minLength", nativeQuery = true)
    List<ProductDetail> findByVoucherCodeLongerThan(@Param("minLength") int minLength);

    // Complex Join Query
    @Query("SELECT pd FROM ProductDetail pd " +
            "JOIN pd.product p " +
            "JOIN pd.specifications ps " +
            "WHERE p.category = :category AND ps.specValue = :specValue")
    List<ProductDetail> findByProductCategoryAndSpecification(
            @Param("category") Category category,
            @Param("specValue") String specValue);
}
