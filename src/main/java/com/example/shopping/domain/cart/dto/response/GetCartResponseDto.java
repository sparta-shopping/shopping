package com.example.shopping.domain.cart.dto.response;

import com.example.shopping.domain.product.entity.Product;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class GetCartResponseDto {
	
	private final Long userId;
	private final Long productId;
	private final Integer quantity;
	private final Integer totalPrice;
	
	public static GetCartResponseDto of(Long userId, Product product, Integer quantity) {
		return new GetCartResponseDto(
			userId,
			product.getId(),
			quantity,
			product.getPrice() * quantity
		);
	}
}
