package com.v01.techgear_server.controller.product;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import com.v01.techgear_server.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.v01.techgear_server.controller.GraphQLExecutor;
import com.v01.techgear_server.service.ProductRatingService;
import com.v01.techgear_server.service.ProductService;
import com.v01.techgear_server.service.searching.services.ProductSearchService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v01/product")
public class ProductController {
    private final ProductRatingService reviewService;
    private final ProductSearchService searchService;
    private final ProductService productService;
    private final GraphQLExecutor executor;

    @PostMapping("/{id}/reviews")
    public CompletableFuture<ResponseEntity<ProductRatingDTO>> submitReview(
            @PathVariable Long id,
            @RequestPart("review") String reviewJson,
            @RequestPart("reviewImage") MultipartFile reviewImage) {
        return CompletableFuture
                .supplyAsync(() -> {
                    try {
                        ObjectMapper objectMapper = new ObjectMapper();
                        ProductRatingDTO reviewDTO = objectMapper.readValue(reviewJson, ProductRatingDTO.class);

                        reviewDTO.setProduct(productService.getProductById(id).join());

                        reviewService.submitReview(reviewDTO, reviewImage).join();
                        return ResponseEntity.status(HttpStatus.CREATED).body(reviewDTO);
                    } catch (Exception e) {
                        log.error("Failed to add review", e);
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
                    }
                });
    }


    @GetMapping("/filter")
    public CompletableFuture<ResponseEntity<Page<ProductFilterSortResponse>>> filterAndSort(
            @ModelAttribute ProductFilterSortRequest request) {
        return productService.productFilteringSorting(request)
                .thenApply(page -> ResponseEntity.ok().body(page))
                .exceptionally(ex -> {
                    log.error("Error in filterAndSort", ex);
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Page.empty());
                });
    }

    @GetMapping("/search")
    public CompletableFuture<ResponseEntity<ProductSearchResponse>> searchProducts(
            @ModelAttribute ProductSearchRequest request) {
        return searchService.searchProduct(request).thenApply(ResponseEntity::ok)
                .exceptionally(ex -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null));
    }

    @DeleteMapping("/{id}/reviews/{reviewId}")
    public CompletableFuture<ResponseEntity<Void>> deleteReviewById(
            @PathVariable Long id,
            @PathVariable Long reviewId) {
        return reviewService.deleteReviewById(id, reviewId)
                .thenApply(ResponseEntity::ok).exceptionally(ex -> {
                    if (log.isErrorEnabled()) {
                        log.error("Error while delete review ID: {} for product ID: {}", reviewId, id);
                    }
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
                });
    }

    /**
     * Create a new product.
     *
     * @param productJson JSON string representing the product to be created.
     * @param image       The image file to be uploaded and associated with the
     *                    product.
     * @return A ResponseEntity containing the created ProductDTO, or a 400 Bad
     *         Request
     *         status if an error occurs.
     */
    @PostMapping("/new")
    public CompletableFuture<ResponseEntity<ProductDTO>> createProduct(
            @RequestPart("product") String productJson,
            @RequestPart("image") MultipartFile image) throws JsonProcessingException {

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
     * 
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
