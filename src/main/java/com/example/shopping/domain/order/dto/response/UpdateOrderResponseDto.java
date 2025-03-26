package com.example.shopping.domain.order.dto.response;

import com.example.shopping.domain.order.entity.Order;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class UpdateOrderResponseDto {
	
	private final Long id;
	private final String state;
	private final LocalDateTime createdAt;
	private final LocalDateTime updatedAt;
	
	public static UpdateOrderResponseDto of(Order order) {
		return new UpdateOrderResponseDto(
			order.getId(),
			order.getState().toString(),
			order.getCreatedAt(),
			order.getUpdatedAt()
		);
	}
}
