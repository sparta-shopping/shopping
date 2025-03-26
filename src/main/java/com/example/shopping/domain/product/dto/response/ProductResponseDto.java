package com.example.shopping.domain.product.dto.response;

import java.time.LocalDateTime;

import com.example.shopping.domain.product.category.Category;
import com.example.shopping.domain.product.entity.Product;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
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
