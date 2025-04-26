package com.v01.techgear_server.product.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.v01.techgear_server.exception.GenericException;
import com.v01.techgear_server.product.dto.*;
import com.v01.techgear_server.product.search.ProductSearchService;
import com.v01.techgear_server.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v01/product")
public class ProductController {
    private final ProductSearchService searchService;
    private final ProductService productService;

    @GetMapping("/category-name")
    public CompletableFuture<ResponseEntity<ProductDTO>> getProductByCategoryName(@RequestParam String categoryName) {
        return productService.getProductByCategory(categoryName)
                .thenApply(ResponseEntity::ok)
                .exceptionally(ex -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null));
    }

    @GetMapping("/filter")
    public CompletableFuture<ResponseEntity<Page<ProductFilterSortResponse>>> filterAndSort(
            @Valid @ModelAttribute ProductFilterSortRequest request) {
        return productService.productFilteringSorting(request)
                .thenApply(page -> ResponseEntity.ok().body(page))
                .exceptionally(ex -> {
                    log.error("Error in filterAndSort", ex);
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(Page.empty());
                });
    }

    @GetMapping("/search")
    public CompletableFuture<ResponseEntity<ProductSearchResponse>> searchProducts(
            @Valid @ModelAttribute ProductSearchRequest request) {
        return searchService.searchProduct(request)
                .thenApply(ResponseEntity::ok)
                .exceptionally(ex -> {
                    log.error("Error in searchProducts", ex);
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(ProductSearchResponse.builder()
                                    .product(Collections.emptyList())
                                    .totalResult(0)
                                    .page(1)
                                    .perPage(10)
                                    .facets(Collections.emptyMap())
                                    .build());
                });
    }

    /**
     * Create a new product.
     *
     * @param productsJson JSON string representing the product to be created.
     * @param images       The image file to be uploaded and associated with the
     *                    product.
     * @return A ResponseEntity containing the created ProductDTO, or a 400 Bad
     * Request
     * status if an error occurs.
     */
    @PostMapping("/new")
    public CompletableFuture<ResponseEntity<List<ProductDTO>>> createProduct(
            @RequestPart("product") String productsJson,
            @RequestPart(value = "image", required = false) List<MultipartFile> images) {

        List<ProductDTO> dtos;
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

            dtos = mapper.readValue(productsJson, new TypeReference<List<ProductDTO>>() {});

            // Create product
            return productService.createProduct(dtos, images != null ? images : Collections.emptyList())
                    .thenApply(ResponseEntity::ok)
                    .exceptionally(ex -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null));
        } catch (IOException e) {
            log.error("Error parsing product JSON", e);
            throw new GenericException("Error parsing product JSON", e);
        }
    }


    /**
     * Deletes a product with the given ID.
     *
     * @param id the ID of the product to delete
     * @return a ResponseEntity indicating success or failure
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
        log.info("Attempting to delete product with id: {}", id);
        CompletableFuture<Boolean> exists = productService.deleteProduct(id).thenApply(v -> true)
                .exceptionally(ex -> false);

        return exists.thenApply(result -> {
            if (Boolean.FALSE.equals(result)) {
                log.warn("Attempted to delete non-existing product with id: {}", id);
                return new ResponseEntity<>("Product not found", HttpStatus.NOT_FOUND);
            }

            log.info("Successfully deleted product with id: {}", id);
            return new ResponseEntity<>("Product deleted successfully", HttpStatus.NO_CONTENT);
        }).join();
    }


}
