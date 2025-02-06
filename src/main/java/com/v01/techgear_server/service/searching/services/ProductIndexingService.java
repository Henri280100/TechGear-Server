package com.v01.techgear_server.service.searching.services;

import java.util.Map;

import com.v01.techgear_server.dto.BulkIndexResult;
import com.v01.techgear_server.model.Product;

public interface ProductIndexingService {

    Map<String, Object> indexProduct(Product product);
    BulkIndexResult bulkIndexProducts();
    void deleteProductFromIndex(Long productId);
}