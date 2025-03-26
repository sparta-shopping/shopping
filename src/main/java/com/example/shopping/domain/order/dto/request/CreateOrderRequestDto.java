package com.example.shopping.domain.order.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateOrderRequestDto {

	private Long couponId;
}
