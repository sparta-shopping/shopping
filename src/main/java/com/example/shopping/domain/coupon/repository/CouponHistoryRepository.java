package com.example.shopping.domain.coupon.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.shopping.domain.coupon.entity.Coupon;
import com.example.shopping.domain.coupon.entity.CouponHistory;
import com.example.shopping.domain.user.entity.User;

public interface CouponHistoryRepository extends JpaRepository<CouponHistory, Long> {
	boolean existsByCouponAndUser(Coupon coupon, User user);

	List<CouponHistory> findByCouponAndUser(Coupon coupon, User user);
}
