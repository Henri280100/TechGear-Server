package com.v01.techgear_server.product.service;

import com.v01.techgear_server.product.dto.ProductCategoryDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface ProductCategoryService {

    /**
     * Updates an existing product category.
     *
     * @param id                 the ID of the product category to update
     * @param productCategoryDTO the updated product category data
     * @return the updated product category
     */
    CompletableFuture<ProductCategoryDTO> updateProductCategory(Long id, ProductCategoryDTO productCategoryDTO, MultipartFile image);

    /**
     * Deletes a product category by its ID.
     *
     * @param id the ID of the product category to delete
     */
    CompletableFuture<Void> deleteProductCategory(Long id);

    // create multiple product categories with multiple images
    /**
     * Creates multiple product categories.
     *
     * @param productCategoryDTOs the list of product categories to create
     * @param image               the image file to be uploaded and associated with the product categories
     * @return the list of created product categories
     */
    CompletableFuture<List<ProductCategoryDTO>> createProductCategories(List<ProductCategoryDTO> productCategoryDTOs, List<MultipartFile> image);

}
