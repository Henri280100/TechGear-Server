package com.v01.techgear_server.product.search;

import com.v01.techgear_server.product.dto.ProductDTO;

import java.util.List;

public interface ProductIndexingService {

    void indexProduct(List<ProductDTO> products);

    void indexSingleProduct(ProductDTO productDTO);
}