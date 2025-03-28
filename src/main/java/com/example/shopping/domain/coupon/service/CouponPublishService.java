package com.example.shopping.domain.coupon.service;

import static com.example.shopping.common.exception.ErrorCode.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.shopping.common.dto.AuthUser;
import com.example.shopping.domain.coupon.entity.Coupon;
import com.example.shopping.domain.coupon.entity.CouponHistory;
import com.example.shopping.domain.coupon.repository.CouponHistoryRepository;
import com.example.shopping.domain.coupon.repository.CouponRepository;
import com.example.shopping.domain.user.entity.User;
import com.example.shopping.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CouponPublishService {

	private final CouponRepository couponRepository;
	private final UserRepository userRepository;
	private final CouponHistoryRepository couponHistoryRepository;

	@Transactional
	public void checkHasCouponAndPublish(AuthUser authUser, Long couponId) {
		Coupon coupon = getCoupon(couponId);
		if (coupon.getStock() == 0) {
			coupon.setDeletedAt();
			throw new ResponseStatusException(EMPTY_COUPON_STOCK.getStatus(), EMPTY_COUPON_STOCK.getMessage());
		}
		User user = getUser(authUser);
		if (couponHistoryRepository.existsByCouponAndUser(coupon, user)) {
			throw new ResponseStatusException(ALREADY_PUBLISHED_COUPON.getStatus(), ALREADY_PUBLISHED_COUPON.getMessage());
		}
		coupon.publishCoupon();
		couponRepository.save(coupon);
		CouponHistory couponHistory = new CouponHistory(user, coupon);
		couponHistoryRepository.save(couponHistory);
	}

	@Transactional(readOnly = true)
	public CouponHistory getCouponHistory(AuthUser authUser, Long couponId) {
		Coupon coupon = getCoupon(couponId);
		User user = getUser(authUser);
		return couponHistoryRepository.findByCouponAndUser(coupon, user);
	}

	private User getUser(AuthUser authUser) {
		return userRepository.findUserById(authUser.getId())
			.orElseThrow(() -> new ResponseStatusException(USER_NOT_FOUND.getStatus(), USER_NOT_FOUND.getMessage()));
	}

	private Coupon getCoupon(Long couponId) {
		return couponRepository.findCouponById(couponId)
			.orElseThrow(
				() -> new ResponseStatusException(COUPON_NOT_FOUND.getStatus(), COUPON_NOT_FOUND.getMessage()));
	}
}
