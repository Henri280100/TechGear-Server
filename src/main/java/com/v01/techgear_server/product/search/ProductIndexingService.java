package com.v01.techgear_server.product.search;

import java.util.Map;

import com.v01.techgear_server.product.model.Product;

public interface ProductIndexingService {

    Map<String, Object> indexProduct(Product product);

}