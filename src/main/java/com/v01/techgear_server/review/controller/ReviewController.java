package com.v01.techgear_server.review.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v01/product")
public class ReviewController {
    //    @PostMapping("/{id}/reviews")
//    public CompletableFuture<ResponseEntity<ProductRatingDTO>> submitReview(
//            @PathVariable Long id,
//            @RequestPart("review") String reviewJson,
//            @RequestPart("reviewImage") MultipartFile reviewImage) {
//        return CompletableFuture
//                .supplyAsync(() -> {
//                    try {
//                        ObjectMapper objectMapper = new ObjectMapper();
//                        ProductRatingDTO reviewDTO = objectMapper.readValue(reviewJson, ProductRatingDTO.class);
//
//                        reviewDTO.setProductId(productService.getProductById(id).join().getId());
//
//                        reviewService.submitReview(reviewDTO, reviewImage).join();
//                        return ResponseEntity.status(HttpStatus.CREATED).body(reviewDTO);
//                    } catch (Exception e) {
//                        log.error("Failed to add review", e);
//                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
//                    }
//                });
//    }

//    @DeleteMapping("/{id}/reviews/{reviewId}")
//    public CompletableFuture<ResponseEntity<Void>> deleteReviewById(
//            @PathVariable Long id,
//            @PathVariable Long reviewId) {
//        return reviewService.deleteReviewById(id, reviewId)
//                .thenApply(ResponseEntity::ok).exceptionally(ex -> {
//                    if (log.isErrorEnabled()) {
//                        log.error("Error while delete review ID: {} for product ID: {}", reviewId, id);
//                    }
//                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
//                });
//    }
}
