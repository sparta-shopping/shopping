package com.example.shopping.domain.coupon.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.shopping.domain.coupon.entity.Coupon;
import com.example.shopping.domain.coupon.entity.CouponHistory;
import com.example.shopping.domain.user.entity.User;

import jakarta.persistence.LockModeType;

public interface CouponHistoryRepository extends JpaRepository<CouponHistory, Long> {
	boolean existsByCouponAndUser(Coupon coupon, User user);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select ch from CouponHistory ch where ch.coupon = :coupon and ch.user = :user")
	Optional<CouponHistory> findCouponHistoryForPessimistic(@Param("coupon") Coupon coupon, @Param("user") User user);

	CouponHistory findByCouponAndUser(Coupon coupon, User user);
}
