package com.example.shopping.domain.coupon.repository;

import com.example.shopping.domain.coupon.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponRepository extends JpaRepository<Coupon, Long>, CouponRepositoryQuery {
}
