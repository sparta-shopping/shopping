package com.example.shopping.domain.order.controller;

import com.example.shopping.common.dto.AuthUser;
import com.example.shopping.domain.order.dto.request.CreateOrderRequestDto;
import com.example.shopping.domain.order.dto.response.CreateOrderResponseDto;
import com.example.shopping.domain.order.service.OrderServiceConcurrency;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrderControllerConcurrency {

	private final OrderServiceConcurrency orderServiceConcurrency;

	@PostMapping("/api/v2/orders")
	public ResponseEntity<CreateOrderResponseDto> saveOrderV2(
		@AuthenticationPrincipal AuthUser authUser,
		@RequestBody CreateOrderRequestDto dto
	) {
		return ResponseEntity.ok(orderServiceConcurrency.saveOrderV2(authUser.getId(), dto));
	}
}
