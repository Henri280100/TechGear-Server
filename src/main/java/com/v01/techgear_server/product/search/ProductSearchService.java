package com.v01.techgear_server.product.search;

import java.util.concurrent.CompletableFuture;

import org.typesense.model.SearchResult;

import com.v01.techgear_server.product.dto.ProductSearchRequest;
import com.v01.techgear_server.product.dto.ProductSearchResponse;

public interface ProductSearchService {
    CompletableFuture<ProductSearchResponse> searchProduct(ProductSearchRequest request);

    ProductSearchResponse buildSearchResponse(SearchResult searchResult);
}
