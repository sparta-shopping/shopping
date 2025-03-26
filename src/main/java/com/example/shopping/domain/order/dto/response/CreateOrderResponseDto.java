package com.example.shopping.domain.order.dto.response;

import com.example.shopping.domain.order.entity.Order;
import com.example.shopping.domain.order.entity.OrderItem;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateOrderResponseDto {
	
	private final Long id;
	private final Long userId;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private final Long couponId;
	private final String state;
	private final Integer totalPrice;
	private final List<OrderItem> orderItems;
	
	public static CreateOrderResponseDto of(Order order) {
		return new CreateOrderResponseDto(
			order.getId(),
			order.getUser().getId(),
			(order.getCoupon() != null) ? order.getCoupon().getId() : null,
			order.getState().toString(),
			order.getTotalPrice(),
			order.getOrderItems()
		);
	}
}
