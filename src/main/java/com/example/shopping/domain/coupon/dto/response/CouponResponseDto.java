package com.example.shopping.domain.coupon.dto.response;

import java.time.LocalDateTime;

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

	private final LocalDateTime createdAt;

	private final LocalDateTime updatedAt;

	private final LocalDateTime deletedAt;

	public static CouponResponseDto of(Coupon coupon) {
		return new CouponResponseDto(
			coupon.getId(),
			coupon.getName(),
			coupon.getDiscountAmount(),
			coupon.getStock(),
			coupon.getCreatedAt(),
			coupon.getUpdatedAt(),
			coupon.getDeletedAt()
		);
	}
}
