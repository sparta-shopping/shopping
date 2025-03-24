package com.example.shopping.domain.review.repository;

import com.example.shopping.domain.review.entity.Review;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import static com.example.shopping.domain.review.entity.QReview.review;

@RequiredArgsConstructor
public class ReviewRepositoryQueryImpl implements ReviewRepositoryQuery {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<Review> findReviewById(Long reviewId) {
        return Optional.ofNullable(jpaQueryFactory.selectFrom(review)
                .where(review.id.eq(reviewId)
                        .and(review.deletedAt.isNull()))
                .fetchOne());
    }
}