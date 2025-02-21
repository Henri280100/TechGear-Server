package com.v01.techgear_server.serviceImpls;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.v01.techgear_server.dto.ImageDTO;
import com.v01.techgear_server.dto.ProductRatingDTO;
import com.v01.techgear_server.exception.BadRequestException;
import com.v01.techgear_server.exception.ResourceNotFoundException;
import com.v01.techgear_server.mapping.ProductRatingMapper;
import com.v01.techgear_server.model.AccountDetails;
import com.v01.techgear_server.model.Image;
import com.v01.techgear_server.model.Product;
import com.v01.techgear_server.repo.jpa.AccountDetailsRepository;
import com.v01.techgear_server.repo.jpa.ProductRatingRepository;
import com.v01.techgear_server.repo.jpa.ProductRepository;
import com.v01.techgear_server.service.FileStorageService;
import com.v01.techgear_server.service.ProductRatingService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductRatingServiceImpl implements ProductRatingService {
    private final ProductRatingRepository productRatingRepository;
    private final ProductRepository productRepository;
    private final AccountDetailsRepository accountDetailsRepository;
    private final FileStorageService fileStorageService;
    private final ProductRatingMapper productRatingMapper;

    @Override
    public CompletableFuture<Void> deleteReviewById(Long productId, Long reviewId) {
        return CompletableFuture.runAsync(() -> {
            if (productId == null || reviewId == null) {
                throw new BadRequestException("Product id and Review id cannot be null");
            }

            boolean productExists = productRepository.existsById(productId);
            if (!productExists) {
                throw new ResourceNotFoundException("Product not found with id " + productId);
            }

            var productRating = productRatingRepository.findByProductIdAndProductRatingId(productId, reviewId)
                    .orElseThrow(() -> new ResourceNotFoundException("Review not found with id " + reviewId + " for product " + productId));

            productRatingRepository.delete(productRating);
        });
    }

    @Override
    public CompletableFuture<List<ProductRatingDTO>> getAllReviewsForProduct(Long productId) {
        return CompletableFuture.supplyAsync(() -> {
            if (productId == null) {
                throw new BadRequestException("Product id cannot be null");
            }

            List<ProductRatingDTO> reviewDtos = new ArrayList<>();
            productRatingRepository.findAllByProductId(productId);
            return reviewDtos;
        });
    }

    @Override
    public CompletableFuture<Double> getAverageRatingForProduct(Long productId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public CompletableFuture<ProductRatingDTO> getReviewById(Long productId, Long reviewId) {
        return CompletableFuture.supplyAsync(() -> {
            if (reviewId == null) {
                throw new BadRequestException("Review id cannot be null");
            }

            if (productId == null) {
                throw new BadRequestException("Product id cannot be null");
            }

            return productRatingRepository.findByProductIdAndProductRatingId(productId, reviewId)
                    .map(productRatingMapper::toDTO)
                    .orElseThrow(() -> new ResourceNotFoundException("Review not found with id " + reviewId + " for product " + productId));
        });
    }

    @Override
    public CompletableFuture<Void> submitReview(ProductRatingDTO reviewDto, MultipartFile file) {
        return CompletableFuture.runAsync(() -> {
            Product product = productRepository.findById(reviewDto.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Product not found with id " + reviewDto.getProductId()));

            AccountDetails accountDetails = accountDetailsRepository
                    .findById(reviewDto.getAccountId())
                    .orElseThrow(() -> new ResourceNotFoundException("User account detail not found with id "
                            + reviewDto.getAccountId()));
            
            
            try {
                Image image = null;
                if (file != null && !file.isEmpty()) {
                    ImageDTO imageDTO = fileStorageService.storeSingleImage(file).join();
                    image = new Image();
                    image.setImageUrl(imageDTO.getImageUrl());
                }

                var productRating = productRatingMapper.toEntity(reviewDto);
                productRating.setProduct(product);
                productRating.setAccountDetails(accountDetails);
                productRating.setReviewImage(image);
                productRating.setComments(reviewDto.getProductComments());

                productRatingRepository.save(productRating);
            } catch (IOException e) {
                throw new BadRequestException("Failed to store file", e);
            }
        });
    }

    @Override
    public CompletableFuture<Void> approveReview(Long reviewId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public CompletableFuture<List<ProductRatingDTO>> getAllPendingReviews() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public CompletableFuture<List<ProductRatingDTO>> getAllReviewsByProductTitle(String productTitle) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public CompletableFuture<List<ProductRatingDTO>> getAllReviewsByRating(Double rating) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public CompletableFuture<List<ProductRatingDTO>> getAllReviewsByUser(Long accountDetailsId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public CompletableFuture<List<ProductRatingDTO>> getAllReviewsByUserName(String username) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public CompletableFuture<Void> rejectReview(Long reviewId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public CompletableFuture<ProductRatingDTO> updateReview(Long reviewId, ProductRatingDTO updatedReview) {
        // TODO Auto-generated method stub
        return null;
    }

    
}
