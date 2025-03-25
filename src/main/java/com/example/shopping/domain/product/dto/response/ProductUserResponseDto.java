package com.example.shopping.domain.product.dto.response;

import java.time.LocalDateTime;

import com.example.shopping.domain.product.entity.ProductUser;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProductUserResponseDto {
	private final Long userId;

	private final String name;

	private final LocalDateTime touchedAt;

	public static ProductUserResponseDto of(ProductUser productUser) {
		return new ProductUserResponseDto(
			productUser.getUser().getId(),
			productUser.getUser().getName(),
			productUser.getTouchedAt()
		);
	}
}
