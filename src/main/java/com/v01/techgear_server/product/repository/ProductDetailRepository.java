package com.v01.techgear_server.product.repository;

import com.v01.techgear_server.product.model.ProductDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductDetailRepository
        extends JpaRepository<ProductDetail, Long>, JpaSpecificationExecutor<ProductDetail> {

}
