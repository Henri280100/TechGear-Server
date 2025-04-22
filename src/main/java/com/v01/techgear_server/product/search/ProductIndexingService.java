package com.v01.techgear_server.product.search;

import java.util.List;
import java.util.Map;

import com.v01.techgear_server.product.dto.ProductDTO;
import com.v01.techgear_server.product.model.Product;

public interface ProductIndexingService {

    void indexProduct(List<ProductDTO> products);

}