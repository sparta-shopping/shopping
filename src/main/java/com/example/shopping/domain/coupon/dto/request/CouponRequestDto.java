package com.example.shopping.domain.coupon.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CouponRequestDto {

	private final String CouponName;

	private final Integer discountAmount;

	private final Integer stock;
}
