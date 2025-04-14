package com.v01.techgear_server.review.graphql.resolver.query;

import java.util.*;

import org.springframework.stereotype.Component;

import com.v01.techgear_server.product.model.ProductRating;
import com.v01.techgear_server.product.repository.ProductRatingRepository;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class ReviewsQueryResolver implements DataFetcher<List<ProductRating>> {
    private final ProductRatingRepository ratingRepository;

    @Override
    public List<ProductRating> get(DataFetchingEnvironment environment) throws Exception {
        log.info("Fetching all product ratings");
        return ratingRepository.findAll();
    }

    public Optional<ProductRating> getReviewsById(DataFetchingEnvironment environment) {
        return fetchReviewsById(environment.getArgument("id"), environment.getArgument("reviewId"));
    }

    private Optional<ProductRating> fetchReviewsById(Object argument, Object argument2) {
        log.info("Fetching reviews by id: {}", argument);
        return ratingRepository.findByProductIdAndProductRatingId((Long) argument, (Long) argument2);
    }

    
}