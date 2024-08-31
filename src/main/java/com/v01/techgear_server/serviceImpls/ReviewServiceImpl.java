package com.v01.techgear_server.serviceImpls;

import java.io.IOException;
import java.util.ArrayList;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.v01.techgear_server.dto.ReviewsDTO;
import com.v01.techgear_server.exception.BadRequestException;
import com.v01.techgear_server.exception.ResourceNotFoundException;
import com.v01.techgear_server.model.Image;
import com.v01.techgear_server.model.Product;
import com.v01.techgear_server.model.Review;
import com.v01.techgear_server.model.User;
import com.v01.techgear_server.repo.ProductRepository;
import com.v01.techgear_server.repo.ReviewRepository;
import com.v01.techgear_server.repo.UserRepository;
import com.v01.techgear_server.service.FileStorageService;
import com.v01.techgear_server.service.ReviewService;

import jakarta.transaction.Transactional;

@Service
public class ReviewServiceImpl implements ReviewService {
    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private ModelMapper modelMapper;

    private static Logger LOGGER = LoggerFactory.getLogger(ReviewServiceImpl.class);

    private void validateReviewDTO(ReviewsDTO reviewsDTO) {
        // Check for invalid review data and throw BadRequestException if needed
        if (reviewsDTO.getComments() == null || reviewsDTO.getComments().isEmpty()) {
            throw new BadRequestException("Review comments cannot be null or empty");
        }
    }

    /**
     * Add a review to an existing product.
     * 
     * @param id         The ID of the product to add the review to.
     * @param reviewsDTO JSON string representing the review to be added.
     * @return A ResponseEntity containing the added ReviewDTO, or a 404 Not
     *         Found status if the product is not found.
     */
    @Override
    @Transactional
    public ReviewsDTO addReview(Long id, ReviewsDTO reviewsDTO, MultipartFile reviewImage) {
        // Find the existing product
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("review not found with id " + id));

        // Find the existing User
        User user = userRepository.findById(reviewsDTO.getUser().getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found" + id));

        try {
            validateReviewDTO(reviewsDTO);

            // Initialize reviews if null
            if (product.getReviews() == null) {
                product.setReviews(new ArrayList<>());
            }
            Image image = fileStorageService.storeImage(reviewImage);
            // Map ReviewsDTO to Review entity
            Review review = modelMapper.map(reviewsDTO, Review.class);

            // Add the review to the product
            product.getReviews().add(review);
            
            review.setUser(user);
            
            review.setComments(reviewsDTO.getComments());
            
            review.setRating(reviewsDTO.getRating());
            
            review.setReviewImage(image);

            review.setProduct(product); // Set the association

            // Save the updated product
            productRepository.save(product);

            // Return the added Review as ReviewsDTO
            return modelMapper.map(review, ReviewsDTO.class);
        } catch (BadRequestException | IOException e) {
            e.getMessage();
            LOGGER.error("Failed to add a review", e);
            throw new BadRequestException("Failed to add a review");
        }

    }
}
