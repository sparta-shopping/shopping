package com.example.shopping.domain.order.dto.response;

import com.example.shopping.domain.order.entity.Order;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class GetOrdersResponseDto {
	
	private final Long id;
	private final Long userId;
	private final String state;
	private final Integer totalPrice;
	
	public static GetOrdersResponseDto of(Order order) {
		return new GetOrdersResponseDto(
			order.getId(),
			order.getUser().getId(),
			order.getState().toString(),
			order.getTotalPrice()
		);
	}
}
