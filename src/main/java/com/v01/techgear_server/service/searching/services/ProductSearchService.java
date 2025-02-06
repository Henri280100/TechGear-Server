package com.v01.techgear_server.service.searching.services;

import java.util.concurrent.CompletableFuture;

import org.typesense.model.SearchResult;

import com.v01.techgear_server.dto.ProductSearchRequest;
import com.v01.techgear_server.dto.ProductSearchResponse;

public interface ProductSearchService {
    CompletableFuture<ProductSearchResponse> searchProduct(ProductSearchRequest request);

    ProductSearchResponse buildSearchResponse(SearchResult searchResult);
}
