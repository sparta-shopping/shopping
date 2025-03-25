package com.example.shopping.domain.coupon.dto.response;

import com.example.shopping.domain.coupon.entity.Coupon;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CouponResponseDto {

	private Long couponId;

	private String couponName;

	private Integer discountAmount;

	private Integer stock;

	public static CouponResponseDto of(Coupon coupon){
		return new CouponResponseDto(
			coupon.getId(),
			coupon.getName(),
			coupon.getDiscountAmount(),
			coupon.getStock()
		);
	}
}
