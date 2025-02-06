package com.v01.techgear_server.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.v01.techgear_server.dto.ProductRatingDTO;

import java.util.concurrent.CompletableFuture;

public interface ProductRatingService {
    CompletableFuture<Void> submitReview(ProductRatingDTO reviewDto, MultipartFile file);

    CompletableFuture<List<ProductRatingDTO>> getAllReviewsForProduct(Long productId);

    CompletableFuture<ProductRatingDTO> getReviewById(Long productId, Long reviewId);

    CompletableFuture<Void> deleteReviewById(Long productId, Long reviewId);

    CompletableFuture<Double> getAverageRatingForProduct(Long productId);


    CompletableFuture<ProductRatingDTO> updateReview(Long reviewId, ProductRatingDTO updatedReview);

    CompletableFuture<List<ProductRatingDTO>> getAllReviewsByUser(Long accountDetailsId);

    CompletableFuture<List<ProductRatingDTO>> getAllReviewsByUserName(String username);

    CompletableFuture<List<ProductRatingDTO>> getAllReviewsByProductTitle(String productTitle);

    CompletableFuture<List<ProductRatingDTO>> getAllReviewsByRating(Double rating);

    CompletableFuture<Void> approveReview(Long reviewId);

    CompletableFuture<Void> rejectReview(Long reviewId);

    CompletableFuture<List<ProductRatingDTO>> getAllPendingReviews();


}
