package com.v01.techgear_server.product.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.v01.techgear_server.product.dto.ProductCategoryDTO;
import com.v01.techgear_server.product.service.ProductCategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v01/category")
public class ProductCategoryController {
    private final ProductCategoryService categoryService;


    @PostMapping("/add")
    public CompletableFuture<ResponseEntity<List<ProductCategoryDTO>>> addProductCategories(
            @RequestPart("category") String categoriesJson,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {

        List<ProductCategoryDTO> dtos;
        try {
            dtos = new ObjectMapper().readValue(
                    categoriesJson,
                    new TypeReference<List<ProductCategoryDTO>>() {
                    });
        } catch (IOException e) {
            log.error("Error parsing JSON for ProductCategoryDTO", e);
            return CompletableFuture.completedFuture(
                    ResponseEntity.badRequest().body(null)
            );
        }

        // Defensive copy to prevent null list
        List<MultipartFile> safeImages = (images != null) ? images : List.of();

        return categoryService.createProductCategories(dtos, safeImages)
                .thenApply(ResponseEntity::ok)
                .exceptionally(ex -> {
                    log.error("Error adding product categories", ex);

                    // Optional: log cause of FileUploadingException if nested
                    Throwable cause = ex.getCause();
                    if (cause != null) {
                        log.error("Root cause: {}", cause.getMessage(), cause);
                    }

                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(null); // You can return a meaningful error DTO here
                });
    }


    @PutMapping("/{id}/update")
    public CompletableFuture<ResponseEntity<ProductCategoryDTO>> updateProductCategory(
            @PathVariable Long id,
            @RequestPart("category") String categoryJson,
            @RequestPart("image") MultipartFile image) {
        try {
            ProductCategoryDTO dto = new ObjectMapper().readValue(categoryJson, ProductCategoryDTO.class);
            return categoryService.updateProductCategory(id, dto, image)
                    .thenApply(ResponseEntity::ok)
                    .exceptionally(ex -> {
                        log.error("Error updating product category", ex);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
                    });
        } catch (IOException e) {
            log.error("Error parsing JSON", e);
            return CompletableFuture.completedFuture(ResponseEntity.badRequest().body(null));
        }
    }

    @DeleteMapping("/{id}/delete")
    public CompletableFuture<ResponseEntity<Object>> deleteProductCategory(@PathVariable Long id) {
        return categoryService.deleteProductCategory(id)
                .thenApply(v -> ResponseEntity.noContent().build())
                .exceptionally(ex -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }

}
