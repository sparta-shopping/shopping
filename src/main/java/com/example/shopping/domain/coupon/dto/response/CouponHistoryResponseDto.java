package com.example.shopping.domain.coupon.dto.response;

import com.example.shopping.domain.coupon.entity.Coupon;
import com.example.shopping.domain.coupon.entity.CouponHistory;
import com.example.shopping.domain.user.entity.User;

import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
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
