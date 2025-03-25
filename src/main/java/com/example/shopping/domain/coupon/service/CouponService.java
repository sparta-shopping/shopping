package com.example.shopping.domain.coupon.service;

import static com.example.shopping.common.exception.ErrorCode.*;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.shopping.common.dto.AuthUser;
import com.example.shopping.common.dto.PageResponseDto;
import com.example.shopping.domain.coupon.dto.request.CouponRequestDto;
import com.example.shopping.domain.coupon.dto.response.CouponHistoryResponseDto;
import com.example.shopping.domain.coupon.dto.response.CouponResponseDto;
import com.example.shopping.domain.coupon.entity.Coupon;
import com.example.shopping.domain.coupon.entity.CouponHistory;
import com.example.shopping.domain.coupon.repository.CouponHistoryRepository;
import com.example.shopping.domain.coupon.repository.CouponRepository;
import com.example.shopping.domain.user.entity.User;
import com.example.shopping.domain.user.repository.UserRepository;
import com.example.shopping.domain.user.role.UserRole;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CouponService {

	private final CouponRepository couponRepository;
	private final UserRepository userRepository;
	private final CouponHistoryRepository couponHistoryRepository;

	@Transactional
	public CouponResponseDto createCoupon(AuthUser authUser, CouponRequestDto dto) {
		User userById = getUser(authUser);

		checkAuthority(userById);

		Coupon coupon = new Coupon(dto.getCouponName() ,dto.getDiscountAmount(), dto.getStock(), userById);

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
	public CouponResponseDto updateCoupon(AuthUser authUser, Long couponId, CouponRequestDto dto) {
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

	@Transactional
	public CouponHistoryResponseDto publishCoupon(AuthUser authUser, Long couponId) {
		Coupon coupon = getCoupon(couponId);

		if (coupon.getStock() == 0) {
			throw new ResponseStatusException(EMPTY_COUPON_STOCK.getStatus(), EMPTY_COUPON_STOCK.getMessage());
		}

		User userById = getUser(authUser);

		if (couponHistoryRepository.existsByCouponAndUser(coupon, userById)) {
			throw new ResponseStatusException(ALREADY_PUBLISHED_COUPON.getStatus(), ALREADY_PUBLISHED_COUPON.getMessage());
		}

		coupon.publishCoupon();

		CouponHistory couponHistory = new CouponHistory(userById, coupon);

		CouponHistory saveCouponHistory = couponHistoryRepository.save(couponHistory);

		return CouponHistoryResponseDto.of(saveCouponHistory);
	}

	@Transactional
	public void useCoupon(Long couponId, User user) {
		List<CouponHistory> couponHistories = couponHistoryRepository.findByCouponAndUser(getCoupon(couponId), user);
		if (couponHistories.isEmpty()) {
			throw new ResponseStatusException(COUPON_NOT_FOUND.getStatus(), COUPON_NOT_FOUND.getMessage());
		}
		if (couponHistories.size() > 1) {
			throw new ResponseStatusException(ALREADY_PUBLISHED_COUPON.getStatus(), ALREADY_PUBLISHED_COUPON.getMessage());
		}
		if (!couponHistories.get(0).getHasCoupon()) {
			throw new ResponseStatusException(AlREADY_USED_COUPON.getStatus(), AlREADY_USED_COUPON.getMessage());
		}
		couponHistories.get(0).useCoupon();
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
}
