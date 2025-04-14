package com.v01.techgear_server.product.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.v01.techgear_server.product.dto.ProductDTO;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import com.v01.techgear_server.product.dto.ProductFilterSortRequest;
import com.v01.techgear_server.product.dto.ProductFilterSortResponse;

public interface ProductService {

    CompletableFuture<ProductDTO> createProduct(ProductDTO productDTO, MultipartFile image);

    CompletableFuture<ProductDTO> updateProduct(Long productId, ProductDTO productDTO, MultipartFile image);

    CompletableFuture<Void> deleteProduct(Long productId);

    CompletableFuture<Page<ProductFilterSortResponse>> productFilteringSorting(ProductFilterSortRequest request);

    CompletableFuture<ProductDTO> getProductById(Long productId);

    CompletableFuture<ProductDTO> getProductByName(String productName);

    CompletableFuture<ProductDTO> getProductByCategory(String category);

    CompletableFuture<ProductDTO> getProductByBrand(String brand);

    CompletableFuture<List<ProductDTO>> getProductByPriceRange(Double minPrice, Double maxPrice);

    CompletableFuture<Integer> checkProductStockLevel(Long productId);

}
