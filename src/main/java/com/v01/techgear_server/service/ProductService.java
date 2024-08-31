package com.v01.techgear_server.service;

import org.springframework.web.multipart.MultipartFile;

import com.v01.techgear_server.dto.ProductDTO;


public interface ProductService {
    // Users only see the product
    // Optional<ProductDTO> getProductById(Long id);
    // List<ProductDTO> getAllProducts();
    
    // String uploadImage(MultipartFile file);
    ProductDTO createProduct(ProductDTO productDTO, MultipartFile image);
    // ProductDTO createProduct(ProductDTO productDTO);
    // ProductDTO updateProduct(Long id, ProductDTO productDTO);
    boolean deleteProduct(Long id);
    // ReviewsDTO addReview(Long id, ReviewsDTO reviewDTO);
}
