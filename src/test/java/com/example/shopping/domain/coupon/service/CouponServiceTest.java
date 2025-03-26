package com.example.shopping.domain.coupon.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;

import com.example.shopping.common.dto.AuthUser;
import com.example.shopping.config.JpaTestConfig;
import com.example.shopping.domain.coupon.dto.request.CouponCreateRequestDto;
import com.example.shopping.domain.coupon.dto.response.CouponHistoryResponseDto;
import com.example.shopping.domain.coupon.dto.response.CouponResponseDto;
import com.example.shopping.domain.coupon.entity.Coupon;
import com.example.shopping.domain.coupon.entity.CouponHistory;
import com.example.shopping.domain.coupon.repository.CouponHistoryRepository;
import com.example.shopping.domain.coupon.repository.CouponRepository;
import com.example.shopping.domain.user.entity.User;
import com.example.shopping.domain.user.repository.UserRepository;
import com.example.shopping.domain.user.role.UserRole;

@Import(JpaTestConfig.class)
@ExtendWith(MockitoExtension.class)
class CouponServiceTest {

	@Mock
	private UserRepository userRepository;
	@Mock
	private CouponRepository couponRepository;
	@Mock
	private CouponHistoryRepository couponHistoryRepository;
	@InjectMocks
	private CouponService couponService;

	@Test
	void 쿠폰_생성_성공() {
		// given
		AuthUser authUser = new AuthUser(1L, "a@a.com", UserRole.ROLE_ADMIN);
		CouponCreateRequestDto dto = new CouponCreateRequestDto("야 싸다!", 10000, 1000);
		User user = new User("a@a.com", "1", "a", "1a", UserRole.ROLE_ADMIN);
		Coupon coupon = new Coupon(dto.getCouponName(), dto.getDiscountAmount(), dto.getStock(), user);

		when(userRepository.findUserById(authUser.getId())).thenReturn(Optional.of(user));
		when(couponRepository.save(any(Coupon.class))).thenReturn(coupon);

		// when
		CouponResponseDto result = couponService.createCoupon(authUser, dto);

		// then
		assertNotNull(result);
	}

	@Test
	void 쿠폰_발행_성공() {
		// given
		Long couponId = 1L;
		Long couponHistoryId = 1L;
		AuthUser authUser = new AuthUser(1L, "user@user.com", UserRole.ROLE_USER);
		User MD = new User("a@a.com", "1", "a", "1a", UserRole.ROLE_ADMIN);
		Coupon coupon = new Coupon("야 싸다!", 10000, 1000, MD);
		User user = new User("user@user.com", "1", "a", "1a", UserRole.ROLE_USER);
		CouponHistory couponHistory = new CouponHistory(user, coupon);

		when(userRepository.findUserById(authUser.getId())).thenReturn(Optional.of(user));
		when(couponRepository.findCouponById(any(Long.class))).thenReturn(Optional.of(coupon));
		when(couponHistoryRepository.existsByCouponAndUser(any(Coupon.class), any(User.class))).thenReturn(false);
		when(couponHistoryRepository.save(any(CouponHistory.class))).thenReturn(couponHistory);

		// when
		CouponHistoryResponseDto result = couponService.publishCoupon(authUser, couponId);

		// then
		assertNotNull(result);
		assertTrue(result.getHasCoupon());
	}
}