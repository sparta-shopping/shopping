package com.example.shopping.domain.review.repository;

import com.example.shopping.domain.review.entity.Review;

import java.util.Optional;

public interface ReviewRepositoryQuery {

    Optional<Review> findReviewById(Long reviewId);
}
