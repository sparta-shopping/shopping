package com.example.shopping.domain.product.dto.response;

import java.time.LocalDateTime;

import com.example.shopping.domain.product.category.Category;
import com.example.shopping.domain.product.entity.Product;
import com.querydsl.core.annotations.QueryProjection;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class ProductResponseDto {

	private final Long id;

	private final String name;

	private final Category category;

	private final Integer price;

	private final Integer reviewCount;

	private final Integer stock;

	private final Double averageRating;

	private final String imageUrl;

	private final LocalDateTime createdAt;

	private final LocalDateTime updatedAt;

	private final LocalDateTime deletedAt;

	public ProductResponseDto(Long id, String name, Category category, Integer price, Integer reviewCount,
		Integer stock,
		Double averageRating, String imageUrl, LocalDateTime createdAt, LocalDateTime updatedAt,
		LocalDateTime deletedAt) {
		this.id = id;
		this.name = name;
		this.category = category;
		this.price = price;
		this.reviewCount = reviewCount;
		this.stock = stock;
		this.averageRating = averageRating;
		this.imageUrl = imageUrl;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
		this.deletedAt = deletedAt;
	}

	public static ProductResponseDto of(Product product) {
		return new ProductResponseDto(
			product.getId(),
			product.getName(),
			product.getCategory(),
			product.getPrice(),
			product.getReviewCount(),
			product.getStock(),
			product.getAverageRating(),
			product.getImageUrl(),
			product.getCreatedAt(),
			product.getUpdatedAt(),
			product.getDeletedAt()
		);
	}
}
