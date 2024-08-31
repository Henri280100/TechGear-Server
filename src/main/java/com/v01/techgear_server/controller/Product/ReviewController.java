package com.v01.techgear_server.controller.Product;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.v01.techgear_server.dto.ReviewsDTO;
import com.v01.techgear_server.service.ReviewService;

@RestController
public class ReviewController {
    @Autowired
    ReviewService reviewService;

    private static Logger LOGGER = LoggerFactory.getLogger(ReviewController.class);

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    /**
     * Adds a review to a product with the given ID.
     *
     * @param id        the ID of the product to add the review to
     * @param reviewDTO the DTO containing the review information
     * @return a ResponseEntity containing the added ReviewDTO if successful,
     *         or a ResponseEntity with a null body and a BAD_REQUEST status code
     *         if the review could not be added
     */
    @SuppressWarnings("null")
    @PostMapping("/api/product/add-review/{id}")
    public ResponseEntity<ReviewsDTO> addReview(
            @PathVariable("id") Long id,
            @RequestPart("review") String reviewJson,
            @RequestPart("reviewImage") MultipartFile reviewImage) {
        if (reviewImage == null || reviewImage.isEmpty()) {
            LOGGER.error("Image file is required");
            return ResponseEntity.badRequest().body(null);
        }

        try {
            ReviewsDTO reviewDTO = new ObjectMapper().readValue(reviewJson, ReviewsDTO.class);
            ReviewsDTO addedReview = reviewService.addReview(id, reviewDTO, reviewImage);

            return new ResponseEntity<>(addedReview, HttpStatus.CREATED);
        } catch (IOException e) {
            LOGGER.error("Failed to process image file: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(null);
        } catch (Exception e) {
            LOGGER.error("Unexpected error occurred while adding review to product with id: {}", id, e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
