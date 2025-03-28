package com.example.shopping.domain.order.service;

import com.example.shopping.common.aop.annotation.RedissonLock;
import com.example.shopping.domain.order.dto.request.CreateOrderRequestDto;
import com.example.shopping.domain.order.dto.response.CreateOrderResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderServiceConcurrency {

	private final OrderService orderService;

	@RedissonLock(value = "orderLock")
	public CreateOrderResponseDto saveOrderV2(Long userId, CreateOrderRequestDto dto) {
		return orderService.saveOrder(userId, dto);
	}
}
