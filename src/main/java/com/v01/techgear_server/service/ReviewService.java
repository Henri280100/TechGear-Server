package com.v01.techgear_server.service;

import org.springframework.web.multipart.MultipartFile;

import com.v01.techgear_server.dto.ReviewsDTO;

//TODO: Implement review service will be store when user submit a review
public interface ReviewService {
    ReviewsDTO addReview(Long id, ReviewsDTO reviewDTO, MultipartFile reviewImage);
}
