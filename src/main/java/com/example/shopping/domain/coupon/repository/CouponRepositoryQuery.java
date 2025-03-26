package com.example.shopping.domain.coupon.repository;

import com.example.shopping.domain.coupon.dto.response.CouponResponseDto;
import com.example.shopping.domain.coupon.entity.Coupon;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CouponRepositoryQuery {

    Optional<Coupon> findCouponById(Long couponId);

    Page<CouponResponseDto> findAllCoupons(Pageable pageable);
}
