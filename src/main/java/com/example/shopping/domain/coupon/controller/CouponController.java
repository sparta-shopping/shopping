package com.example.shopping.domain.coupon.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.shopping.common.dto.AuthUser;
import com.example.shopping.common.dto.PageResponseDto;
import com.example.shopping.domain.coupon.dto.request.CouponCreateRequestDto;
import com.example.shopping.domain.coupon.dto.request.CouponUpdateRequestDto;
import com.example.shopping.domain.coupon.dto.response.CouponHistoryResponseDto;
import com.example.shopping.domain.coupon.dto.response.CouponResponseDto;
import com.example.shopping.domain.coupon.service.CouponService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class CouponController {

	private final CouponService couponService;

	@PostMapping("/api/v1/coupons")
	public ResponseEntity<CouponResponseDto> createCoupon(
		@AuthenticationPrincipal AuthUser authUser,
		@Valid @RequestBody CouponCreateRequestDto dto
	) {
		return ResponseEntity.ok(couponService.createCoupon(authUser, dto));
	}

	@GetMapping("/api/v1/coupons/{couponId}")
	public ResponseEntity<CouponResponseDto> findCoupon(
		@PathVariable Long couponId
	) {
		return ResponseEntity.ok(couponService.findCoupon(couponId));
	}

	@GetMapping("/api/v1/coupons")
	public ResponseEntity<PageResponseDto<CouponResponseDto>> findCoupons(
		@PageableDefault(page = 1, size = 10) Pageable pageable
	) {
		Pageable convertPageable = PageRequest.of(pageable.getPageNumber() - 1, pageable.getPageSize());
		return ResponseEntity.ok(couponService.findCoupons(convertPageable));
	}

	@PatchMapping("/api/v1/coupons/{couponId}")
	public ResponseEntity<CouponResponseDto> updateCoupon(
		@AuthenticationPrincipal AuthUser authUser,
		@PathVariable Long couponId,
		@Valid @RequestBody CouponUpdateRequestDto dto
	) {
		return ResponseEntity.ok(couponService.updateCoupon(authUser, couponId, dto));
	}

	@DeleteMapping("/api/v1/coupons/{couponId}")
	public void deleteCoupon(
		@AuthenticationPrincipal AuthUser authUser,
		@PathVariable Long couponId
	) {
		couponService.deleteCoupon(authUser, couponId);
	}

	// 1. 기본 형
	@PatchMapping("/api/v1/coupon/{couponId}/publish/basic")
	public ResponseEntity<CouponHistoryResponseDto> publishCoupon(
		@AuthenticationPrincipal AuthUser authUser,
		@PathVariable Long couponId
	) {
		return ResponseEntity.ok(couponService.publishCoupon(authUser, couponId));
	}

	// 2. 비관적 락 (Pessimistic Lock)
	// @Lock(LockModeType.PESSIMISTIC_WRITE)
	@PatchMapping("/api/v1/coupon/{couponId}/publish/pessimistic")
	public ResponseEntity<CouponHistoryResponseDto> publishPessimistic(
		@AuthenticationPrincipal AuthUser authUser,
		@PathVariable Long couponId
	) {
		return ResponseEntity.ok(couponService.publishCouponPessimistic(authUser, couponId));
	}

	// 3. 분산 락 (Distributed Lock)d
	// Lettuce
	@PatchMapping("/api/v1/coupon/{couponId}/publish/distributed")
	public ResponseEntity<CouponHistoryResponseDto> publishDistributed(
		@AuthenticationPrincipal AuthUser authUser,
		@PathVariable Long couponId
	) {
		return ResponseEntity.ok(couponService.publishCouponDistributed(authUser, couponId));
	}

	// 4. 공정 락 (Fair Lock)
	// RedissonClient
	@PatchMapping("/api/v1/coupon/{couponId}/publish/fair")
	public ResponseEntity<CouponHistoryResponseDto> publishFair(
		@AuthenticationPrincipal AuthUser authUser,
		@PathVariable Long couponId
	) {
		return ResponseEntity.ok(couponService.publishCouponFair(authUser, couponId));
	}

}
