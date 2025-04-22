package com.v01.techgear_server.product.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.v01.techgear_server.graphql.GraphQLExecutor;
import com.v01.techgear_server.product.dto.*;
import com.v01.techgear_server.product.search.ProductSearchService;
import com.v01.techgear_server.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v01/product")
public class ProductController {
    @Autowired
    private final ProductSearchService searchService;
    private final ProductService productService;
    private final GraphQLExecutor executor;


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
     * @param productJson JSON string representing the product to be created.
     * @param image       The image file to be uploaded and associated with the
     *                    product.
     * @return A ResponseEntity containing the created ProductDTO, or a 400 Bad
     * Request
     * status if an error occurs.
     */
    @PostMapping("/new")
    public CompletableFuture<ResponseEntity<ProductDTO>> createProduct(
            @RequestPart("product") String productJson,
            @RequestPart("image") MultipartFile image) throws IOException {

        // Check if the image is empty
        if (image == null || image.isEmpty()) {
            return CompletableFuture.completedFuture(ResponseEntity.badRequest().body(null));
        }

        // Parse ProductDTO from JSON
        ProductDTO productDTO = new ObjectMapper().readValue(productJson, ProductDTO.class);

        // Create product
        return productService.createProduct(productDTO, image)
                .thenApply(ResponseEntity::ok)
                .exceptionally(ex -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null));
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

    @PostMapping(value = "/product/name")
    @QueryMapping(name = "getProductByName")
    public String getProductByName(@RequestBody String queryVal) throws IOException {
        return executor.executeGraphQLQuery(queryVal);
    }

    @PostMapping(value = "/product/id:{productId}")
    @QueryMapping(name = "getProductById")
    public String getProductByID(@PathVariable Long productId, @RequestBody String queryVal) throws IOException {
        return executor.executeGraphQLQuery(queryVal);
    }

    @PostMapping("/product/{id}/reviews")
    @QueryMapping(name = "getAllReviewsForProduct")
    public String getAllReviewsForProduct(
            @PathVariable Long id, @RequestBody String queryVal) throws IOException {
        return executor.executeGraphQLQuery(queryVal);
    }

    @PostMapping("/product/{id}/reviews/{reviewId}")
    @QueryMapping(name = "getReviewById")
    public String getReviewById(
            @PathVariable Long id,
            @PathVariable Long reviewId,
            @RequestBody String queryVal) throws IOException {
        return executor.executeGraphQLQuery(queryVal);
    }

}
