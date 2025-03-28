package com.example.shopping.domain.coupon.repository;

import java.util.Optional;

import com.example.shopping.domain.coupon.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;

public interface CouponRepository extends JpaRepository<Coupon, Long>, CouponRepositoryQuery {

	// 3. 비관적 락 (Pessimistic Lock)
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select c from Coupon c where c.id = :id and c.deletedAt is null ")
	Optional<Coupon> findCouponByIdForPessimisticLock(@Param("id") Long couponId);
}
