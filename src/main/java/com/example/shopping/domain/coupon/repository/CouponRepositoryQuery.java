package com.example.shopping.domain.coupon.repository;

import com.example.shopping.domain.coupon.entity.Coupon;

import java.util.Optional;

public interface CouponRepositoryQuery {

    Optional<Coupon> findCouponById(Long couponId);
}
