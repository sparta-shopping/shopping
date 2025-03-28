package com.example.shopping.domain.coupon.service;

import static com.example.shopping.common.exception.ErrorCode.*;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.shopping.common.dto.AuthUser;
import com.example.shopping.common.dto.PageResponseDto;
import com.example.shopping.common.util.LockManager;
import com.example.shopping.domain.coupon.dto.request.CouponCreateRequestDto;
import com.example.shopping.domain.coupon.dto.request.CouponUpdateRequestDto;
import com.example.shopping.domain.coupon.dto.response.CouponHistoryResponseDto;
import com.example.shopping.domain.coupon.dto.response.CouponResponseDto;
import com.example.shopping.domain.coupon.entity.Coupon;
import com.example.shopping.domain.coupon.entity.CouponHistory;
import com.example.shopping.domain.coupon.repository.CouponHistoryRepository;
import com.example.shopping.domain.coupon.repository.CouponRepository;
import com.example.shopping.domain.user.entity.User;
import com.example.shopping.domain.user.repository.UserRepository;
import com.example.shopping.domain.user.role.UserRole;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CouponService {

	private final CouponRepository couponRepository;
	private final UserRepository userRepository;
	private final CouponHistoryRepository couponHistoryRepository;
	private final LockManager lockManager;
	private final CouponPublishService couponPublishService;

	@Transactional
	public CouponResponseDto createCoupon(AuthUser authUser, CouponCreateRequestDto dto) {
		User userById = getUser(authUser);

		checkAuthority(userById);

		Coupon coupon = new Coupon(dto.getCouponName(), dto.getDiscountAmount(), dto.getStock(), userById);

		Coupon saveCoupon = couponRepository.save(coupon);

		return CouponResponseDto.of(saveCoupon);
	}

	@Transactional(readOnly = true)
	public CouponResponseDto findCoupon(Long couponId) {
		Coupon coupon = getCoupon(couponId);

		return CouponResponseDto.of(coupon);
	}

	@Transactional(readOnly = true)
	public PageResponseDto<CouponResponseDto> findCoupons(Pageable pageable) {
		Page<CouponResponseDto> allCoupons = couponRepository.findAllCoupons(pageable);

		return new PageResponseDto<>(allCoupons);
	}

	@Transactional
	public CouponResponseDto updateCoupon(AuthUser authUser, Long couponId, CouponUpdateRequestDto dto) {
		User userById = getUser(authUser);

		checkAuthority(userById);

		Coupon coupon = getCoupon(couponId);

		if (coupon.getUser() != userById) {
			throw new ResponseStatusException(NOT_SAME_MD.getStatus(), NOT_SAME_MD.getMessage());
		}

		coupon.updateCoupon(dto);

		return CouponResponseDto.of(coupon);
	}

	@Transactional
	public void deleteCoupon(AuthUser authUser, Long couponId) {
		User userById = getUser(authUser);

		checkAuthority(userById);

		Coupon coupon = getCoupon(couponId);

		if (coupon.getUser() != userById) {
			throw new ResponseStatusException(NOT_SAME_MD.getStatus(), NOT_SAME_MD.getMessage());
		}

		coupon.setDeletedAt();
	}

	// 1. 기본 형
	@Transactional
	public CouponHistoryResponseDto publishCoupon(AuthUser authUser, Long couponId) {
		couponPublishService.checkHasCouponAndPublish(authUser, couponId);

		CouponHistory couponHistory = couponPublishService.getCouponHistory(authUser, couponId);

		return CouponHistoryResponseDto.of(couponHistory);
	}

	// 2. 비관적 락 (Pessimistic Lock)
	// @Lock(LockModeType.PESSIMISTIC_WRITE)
	@Transactional
	public CouponHistoryResponseDto publishCouponPessimistic(AuthUser authUser, Long couponId) {
		// 쿠폰 조회에 PESSIMISTIC 락을 걸어 조회부터 원천 차단.
		Coupon coupon = couponRepository.findCouponByIdForPessimisticLock(couponId)
			.orElseThrow(
				() -> new ResponseStatusException(COUPON_NOT_FOUND.getStatus(), COUPON_NOT_FOUND.getMessage()));

		if (coupon.getStock() == 0) {
			coupon.setDeletedAt();
			throw new ResponseStatusException(EMPTY_COUPON_STOCK.getStatus(), EMPTY_COUPON_STOCK.getMessage());
		}

		User user = getUser(authUser);

		// 쿠폰 발급 내역을 PESSIMISTIC 락을 걸어 조회하여, 중복 발급 여부를 원자적으로 확인
		if (couponHistoryRepository.findCouponHistoryForPessimistic(coupon, user).isPresent()) {
			throw new ResponseStatusException(ALREADY_PUBLISHED_COUPON.getStatus(),
				ALREADY_PUBLISHED_COUPON.getMessage());
		}

		coupon.publishCoupon();

		couponRepository.save(coupon);

		CouponHistory couponHistory = new CouponHistory(user, coupon);

		CouponHistory savedHistory = couponHistoryRepository.save(couponHistory);

		return CouponHistoryResponseDto.of(savedHistory);
	}

	// 3. 분산 락 (Distributed Lock)
	// Lettuce
	public CouponHistoryResponseDto publishCouponDistributed(AuthUser authUser, Long couponId) {
		try {
			lockManager.executeWithDistributedLock(couponId,
				() -> couponPublishService.checkHasCouponAndPublish(authUser, couponId));
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new ResponseStatusException(FAILED_TO_GAIN_LOCK.getStatus(), FAILED_TO_GAIN_LOCK.getMessage());
		}

		CouponHistory couponHistory = couponPublishService.getCouponHistory(authUser, couponId);

		return CouponHistoryResponseDto.of(couponHistory);
	}

	// 4. 공정 락 (Fair Lock)
	// RedissonClient
	public CouponHistoryResponseDto publishCouponFair(AuthUser authUser, Long couponId) {
		try {
			lockManager.executeWithFairLock(couponId,
				() -> couponPublishService.checkHasCouponAndPublish(authUser, couponId));
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new ResponseStatusException(FAILED_TO_GAIN_LOCK.getStatus(), FAILED_TO_GAIN_LOCK.getMessage());
		}

		CouponHistory couponHistory = couponPublishService.getCouponHistory(authUser, couponId);

		return CouponHistoryResponseDto.of(couponHistory);
	}

	private User getUser(AuthUser authUser) {
		return userRepository.findUserById(authUser.getId())
			.orElseThrow(() -> new ResponseStatusException(USER_NOT_FOUND.getStatus(), USER_NOT_FOUND.getMessage()));
	}

	private void checkAuthority(User userById) {
		if (userById.getRole() != UserRole.ROLE_ADMIN) {
			throw new ResponseStatusException(USER_ACCESS_DENIED.getStatus(), USER_ACCESS_DENIED.getMessage());
		}
	}

	private Coupon getCoupon(Long couponId) {
		return couponRepository.findCouponById(couponId)
			.orElseThrow(
				() -> new ResponseStatusException(COUPON_NOT_FOUND.getStatus(), COUPON_NOT_FOUND.getMessage()));
	}

	@Transactional
	public void useCoupon(Long couponId, User user) {
		CouponHistory couponHistory = couponHistoryRepository.findByCouponAndUser(getCoupon(couponId), user);
		if (couponHistory == null) {
			throw new ResponseStatusException(COUPON_NOT_FOUND.getStatus(), COUPON_NOT_FOUND.getMessage());
		}
		if (!couponHistory.getHasCoupon()) {
			throw new ResponseStatusException(AlREADY_USED_COUPON.getStatus(), AlREADY_USED_COUPON.getMessage());
		}
		couponHistory.useCoupon();
	}
}
