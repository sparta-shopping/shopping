package com.example.shopping.domain.coupon.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.shopping.common.dto.AuthUser;
import com.example.shopping.domain.coupon.dto.request.CouponRequestDto;
import com.example.shopping.domain.coupon.dto.response.CouponHistoryResponseDto;
import com.example.shopping.domain.coupon.dto.response.CouponResponseDto;
import com.example.shopping.domain.coupon.service.CouponService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class CouponController {

	private final CouponService couponService;

	@PostMapping("/api/v1/coupon")
	public ResponseEntity<CouponResponseDto> createCoupon(
		@AuthenticationPrincipal AuthUser authUser,
		@RequestBody CouponRequestDto dto
	) {
		return ResponseEntity.ok(couponService.createCoupon(authUser, dto));
	}

	@PatchMapping("/api/v1/coupon/{couponId}/publish")
	public ResponseEntity<CouponHistoryResponseDto> publishCoupon(
		@AuthenticationPrincipal AuthUser authUser,
		@PathVariable Long couponId
	) {
		return ResponseEntity.ok(couponService.publishCoupon(authUser, couponId));
	}
}
