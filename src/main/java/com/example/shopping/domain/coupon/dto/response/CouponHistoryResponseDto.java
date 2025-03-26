package com.example.shopping.domain.coupon.dto.response;

import java.time.LocalDateTime;

import com.example.shopping.domain.coupon.entity.CouponHistory;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CouponHistoryResponseDto {

	private Long id;

	private Long userId;

	private String userName;

	private Long couponId;

	private String couponName;

	private Boolean hasCoupon;

	private LocalDateTime createdAt;

	private LocalDateTime updatedAt;

	public static CouponHistoryResponseDto of(CouponHistory couponHistory) {
		return new CouponHistoryResponseDto(
			couponHistory.getId(),
			couponHistory.getUser().getId(),
			couponHistory.getUser().getName(),
			couponHistory.getCoupon().getId(),
			couponHistory.getCoupon().getName(),
			couponHistory.getHasCoupon(),
			couponHistory.getCreatedAt(),
			couponHistory.getUpdatedAt()
		);
	}
}
