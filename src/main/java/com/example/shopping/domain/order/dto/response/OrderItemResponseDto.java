package com.example.shopping.domain.order.dto.response;

import com.example.shopping.domain.order.entity.OrderItem;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderItemResponseDto {
	
	private final Long productId;
	private final Integer quantity;
	private final Integer price;
	
	public static OrderItemResponseDto of(OrderItem orderItem) {
		return new OrderItemResponseDto(
			orderItem.getProductId(),
			orderItem.getQuantity(),
			orderItem.getPrice()
		);
	}
}
