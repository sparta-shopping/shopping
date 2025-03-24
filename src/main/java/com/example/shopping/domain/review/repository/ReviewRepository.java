package com.example.shopping.domain.review.repository;

import com.example.shopping.domain.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long>, ReviewRepositoryQuery {
}
