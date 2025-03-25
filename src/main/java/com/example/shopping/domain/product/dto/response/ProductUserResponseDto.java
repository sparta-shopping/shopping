package com.example.shopping.domain.product.dto.response;

import java.time.LocalDateTime;

import com.example.shopping.domain.product.entity.ProductTouchMD;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProductUserResponseDto {
	private final Long userId;

	private final String name;

	private final LocalDateTime touchedAt;

	public static ProductUserResponseDto of(ProductTouchMD productTouchMD) {
		return new ProductUserResponseDto(
			productTouchMD.getTouchedUser().getId(),
			productTouchMD.getTouchedUser().getName(),
			productTouchMD.getTouchedAt()
		);
	}
}
