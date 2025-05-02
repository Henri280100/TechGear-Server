package com.v01.techgear_server.product.service;

import com.v01.techgear_server.product.dto.ProductDTO;
import com.v01.techgear_server.product.dto.ProductDetailDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface ProductService {

    CompletableFuture<List<ProductDTO>> createProduct(List<ProductDTO> productDTOs, List<MultipartFile> images);

    CompletableFuture<ProductDTO> updateProduct(Long productId, ProductDTO productDTO, MultipartFile image);

    CompletableFuture<List<ProductDetailDTO>> createProductDetail(List<ProductDetailDTO> productDetailDTOs, List<MultipartFile> detailImages, List<MultipartFile> videos);

    CompletableFuture<Void> deleteProduct(Long productId);


    CompletableFuture<ProductDTO> getProductByCategory(String category);

    CompletableFuture<ProductDTO> getProductByBrand(String brand);

    CompletableFuture<List<ProductDTO>> getProductByPriceRange(Double minPrice, Double maxPrice);

    CompletableFuture<Integer> checkProductStockLevel(Long productId);

}
