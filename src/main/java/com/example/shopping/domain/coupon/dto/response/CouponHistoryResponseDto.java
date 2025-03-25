package com.example.shopping.domain.coupon.dto.response;

import com.example.shopping.domain.coupon.entity.CouponHistory;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CouponHistoryResponseDto {

	private Long id;

	private String userName;

	private Long couponId;

	private String couponName;

	private Boolean hasCoupon;

	public static CouponHistoryResponseDto of(CouponHistory couponHistory) {
		return new CouponHistoryResponseDto(
			couponHistory.getId(),
			couponHistory.getUser().getName(),
			couponHistory.getCoupon().getId(),
			couponHistory.getCoupon().getName(),
			couponHistory.getHasCoupon()
		);
	}
}
