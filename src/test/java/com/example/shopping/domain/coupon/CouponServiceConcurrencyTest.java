package com.example.shopping.domain.coupon;

import static org.junit.jupiter.api.Assertions.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.example.shopping.common.dto.AuthUser;
import com.example.shopping.common.util.JwtUtil;
import com.example.shopping.common.util.LockManagerImpl;
import com.example.shopping.domain.coupon.entity.Coupon;
import com.example.shopping.domain.coupon.entity.CouponHistory;
import com.example.shopping.domain.coupon.repository.CouponHistoryRepository;
import com.example.shopping.domain.coupon.repository.CouponRepository;
import com.example.shopping.domain.coupon.service.CouponPublishService;
import com.example.shopping.domain.coupon.service.CouponService;
import com.example.shopping.domain.user.entity.User;
import com.example.shopping.domain.user.repository.UserRepository;
import com.example.shopping.domain.user.role.UserRole;

@SpringBootTest
public class CouponServiceConcurrencyTest {

	@MockitoBean
	private JwtUtil jwtUtil;

	@Autowired
	private CouponService couponService;

	@Autowired
	private CouponPublishService couponPublishService;

	@Autowired
	private LockManagerImpl lockManagerImpl;

	@Autowired
	private CouponRepository couponRepository;

	@Autowired
	private CouponHistoryRepository couponHistoryRepository;

	@Autowired
	private UserRepository userRepository;

	private Coupon testCoupon;
	private AuthUser testAuthUser;
	private User testUser;

	@BeforeEach
	public void setup() {

		couponHistoryRepository.deleteAll();
		couponRepository.deleteAll();
		userRepository.deleteAll();

		// 테스트용 User 생성 (테스트 환경에 맞게 수정)
		testUser = new User("testuser@example.com", "password", "Test User", "Test", UserRole.ROLE_ADMIN);
		testUser = userRepository.save(testUser);
		testAuthUser = new AuthUser(testUser.getId(), testUser.getEmail(), testUser.getRole());

		// 초기 stock이 10인 Coupon 생성
		testCoupon = new Coupon("TestCoupon", 1000, 10, testUser);
		testCoupon = couponRepository.save(testCoupon);

	}

	@Test
	public void 기본형_동시성_발급() throws InterruptedException {
		int attempts = 1000; // 1000번의 시도를 진행합니다.
		ExecutorService executor = Executors.newFixedThreadPool(20);
		CountDownLatch latch = new CountDownLatch(attempts);
		// 성공한 발급 건수를 세기 위한 변수
		AtomicInteger successCount = new AtomicInteger(0);
		// 새로운 사용자 이메일에 사용할 카운터
		AtomicInteger userCounter = new AtomicInteger(0);

		Queue<Integer> procedure = new LinkedList<>();

		long startTime = System.currentTimeMillis();

		for (int i = 0; i < attempts; i++) {
			executor.submit(() -> {
				try {
					// 매번 고유한 사용자 생성
					int userIdNumber = userCounter.incrementAndGet();
					String email = "user" + userIdNumber + "@example.com";
					User newUser = new User(email, "password", "User" + userIdNumber + " ", "Test", UserRole.ROLE_ADMIN);
					newUser = userRepository.save(newUser);
					AuthUser newAuthUser = new AuthUser(newUser.getId(), newUser.getEmail(), newUser.getRole());
					// 쿠폰 발급 시도 (쿠폰 stock은 내부에서 1씩 감소)
					couponService.publishCoupon(newAuthUser, testCoupon.getId());
					successCount.incrementAndGet();
					procedure.add(userIdNumber);
				} catch (Exception e) {
					// 재고 소진이나 중복 발급 등으로 실패하면 예외가 발생할 수 있음
					System.out.println("Exception: " + e.getMessage());
				} finally {
					latch.countDown();
				}
			});
		}
		latch.await();
		executor.shutdown();

		long endTime = System.currentTimeMillis();
		long durationInMillis = endTime - startTime;
		double durationInSeconds = durationInMillis / 1000.0;

		// 최종 쿠폰 stock과 발급 건수를 확인합니다.
		Coupon finalCoupon = couponRepository.findById(testCoupon.getId()).orElse(null);
		int finalStock = finalCoupon != null ? finalCoupon.getStock() : -1;
		// testCoupon에 해당하는 쿠폰 발급 기록만 필터링
		List<CouponHistory> allHistories = couponHistoryRepository.findAll();
		long couponHistoryCount = allHistories.stream()
			.filter(ch -> ch.getCoupon().getId().equals(testCoupon.getId()))
			.count();

		System.out.println("쿠폰 발급 횟수: " + successCount.get());
		System.out.println("최종 쿠폰 수량: " + finalStock);
		System.out.println("발급받은 유저 수: " + couponHistoryCount);
		System.out.println("테스트 실행 시간: " + durationInSeconds + "초");
		System.out.println("쿠폰 발급밭은 순서: ");
		while (!procedure.isEmpty()) {
			System.out.print(" " + procedure.poll() + " ");
		}

		// 초기 쿠폰 stock이 10이라면 최대 10건만 발급되어야 합니다.
		assertEquals(0, finalStock, "모든 쿠폰이 소진되어야 합니다.");
		assertNotEquals(10, couponHistoryCount, "쿠폰 발급 기록은 초기 stock 10건이어야 합니다.");
	}

	@Test
	public void 비관적락_동시성_발급() throws InterruptedException {
		int attempts = 1000; // 1000번의 시도를 진행합니다.
		ExecutorService executor = Executors.newFixedThreadPool(20);
		CountDownLatch latch = new CountDownLatch(attempts);
		// 성공한 발급 건수를 세기 위한 변수
		AtomicInteger successCount = new AtomicInteger(0);
		// 새로운 사용자 이메일에 사용할 카운터
		AtomicInteger userCounter = new AtomicInteger(0);

		Queue<Integer> procedure = new LinkedList<>();

		long startTime = System.currentTimeMillis();

		for (int i = 0; i < attempts; i++) {
			executor.submit(() -> {
				try {
					// 매번 고유한 사용자 생성
					int userIdNumber = userCounter.incrementAndGet();
					String email = "user" + userIdNumber + "@example.com";
					User newUser = new User(email, "password", "User" + userIdNumber + " ", "Test", UserRole.ROLE_ADMIN);
					newUser = userRepository.save(newUser);
					AuthUser newAuthUser = new AuthUser(newUser.getId(), newUser.getEmail(), newUser.getRole());
					// 쿠폰 발급 시도 (쿠폰 stock은 내부에서 1씩 감소)
					couponService.publishCouponPessimistic(newAuthUser, testCoupon.getId());
					successCount.incrementAndGet();
					procedure.add(userIdNumber);
				} catch (Exception e) {
					// 재고 소진이나 중복 발급 등으로 실패하면 예외가 발생할 수 있음
					System.out.println("Exception: " + e.getMessage());
				} finally {
					latch.countDown();
				}
			});
		}
		latch.await();
		executor.shutdown();

		long endTime = System.currentTimeMillis();
		long durationInMillis = endTime - startTime;
		double durationInSeconds = durationInMillis / 1000.0;

		// 최종 쿠폰 stock과 발급 건수를 확인합니다.
		Coupon finalCoupon = couponRepository.findById(testCoupon.getId()).orElse(null);
		int finalStock = finalCoupon != null ? finalCoupon.getStock() : -1;
		// testCoupon에 해당하는 쿠폰 발급 기록만 필터링
		List<CouponHistory> allHistories = couponHistoryRepository.findAll();
		long couponHistoryCount = allHistories.stream()
			.filter(ch -> ch.getCoupon().getId().equals(testCoupon.getId()))
			.count();

		System.out.println("쿠폰 발급 횟수: " + successCount.get());
		System.out.println("최종 쿠폰 수량: " + finalStock);
		System.out.println("발급받은 유저 수: " + couponHistoryCount);
		System.out.println("테스트 실행 시간: " + durationInSeconds + "초");
		System.out.println("쿠폰 발급밭은 순서: ");
		while (!procedure.isEmpty()) {
			System.out.print(" " + procedure.poll() + " ");
		}

		// 초기 쿠폰 stock이 10이라면 최대 10건만 발급되어야 합니다.
		assertEquals(0, finalStock, "모든 쿠폰이 소진되어야 합니다.");
		assertEquals(10, couponHistoryCount, "쿠폰 발급 기록은 초기 stock 10건이어야 합니다.");
	}

	@Test
	public void 분산락_동시성_발급() throws InterruptedException {
		int attempts = 1000; // 1000번의 시도를 진행합니다.
		ExecutorService executor = Executors.newFixedThreadPool(20);
		CountDownLatch latch = new CountDownLatch(attempts);
		// 성공한 발급 건수를 세기 위한 변수
		AtomicInteger successCount = new AtomicInteger(0);
		// 새로운 사용자 이메일에 사용할 카운터
		AtomicInteger userCounter = new AtomicInteger(0);

		Queue<Integer> procedure = new LinkedList<>();

		long startTime = System.currentTimeMillis();

		for (int i = 0; i < attempts; i++) {
			executor.submit(() -> {
				try {
					// 매번 고유한 사용자 생성
					int userIdNumber = userCounter.incrementAndGet();
					String email = "user" + userIdNumber + "@example.com";
					User newUser = new User(email, "password", "User" + userIdNumber + " ", "Test", UserRole.ROLE_ADMIN);
					newUser = userRepository.save(newUser);
					AuthUser newAuthUser = new AuthUser(newUser.getId(), newUser.getEmail(), newUser.getRole());
					// 쿠폰 발급 시도 (쿠폰 stock은 내부에서 1씩 감소)
					couponService.publishCouponDistributed(newAuthUser, testCoupon.getId());
					successCount.incrementAndGet();
					procedure.add(userIdNumber);
				} catch (Exception e) {
					// 재고 소진이나 중복 발급 등으로 실패하면 예외가 발생할 수 있음
					System.out.println("Exception: " + e.getMessage());
				} finally {
					latch.countDown();
				}
			});
		}
		latch.await();
		executor.shutdown();

		long endTime = System.currentTimeMillis();
		long durationInMillis = endTime - startTime;
		double durationInSeconds = durationInMillis / 1000.0;

		// 최종 쿠폰 stock과 발급 건수를 확인합니다.
		Coupon finalCoupon = couponRepository.findById(testCoupon.getId()).orElse(null);
		int finalStock = finalCoupon != null ? finalCoupon.getStock() : -1;
		// testCoupon에 해당하는 쿠폰 발급 기록만 필터링
		List<CouponHistory> allHistories = couponHistoryRepository.findAll();
		long couponHistoryCount = allHistories.stream()
			.filter(ch -> ch.getCoupon().getId().equals(testCoupon.getId()))
			.count();

		System.out.println("쿠폰 발급 횟수: " + successCount.get());
		System.out.println("최종 쿠폰 수량: " + finalStock);
		System.out.println("발급받은 유저 수: " + couponHistoryCount);
		System.out.println("테스트 실행 시간: " + durationInSeconds + "초");
		System.out.println("쿠폰 발급밭은 순서: ");
		while (!procedure.isEmpty()) {
			System.out.print(" " + procedure.poll() + " ");
		}

		// 초기 쿠폰 stock이 10이라면 최대 10건만 발급되어야 합니다.
		assertEquals(0, finalStock, "모든 쿠폰이 소진되어야 합니다.");
		assertEquals(10, couponHistoryCount, "쿠폰 발급 기록은 초기 stock 10건이어야 합니다.");
	}

	@Test
	public void 공정락_동시성_발급() throws InterruptedException {
		int attempts = 1000; // 1000번의 시도를 진행합니다.
		ExecutorService executor = Executors.newFixedThreadPool(20);
		CountDownLatch latch = new CountDownLatch(attempts);
		// 성공한 발급 건수를 세기 위한 변수
		AtomicInteger successCount = new AtomicInteger(0);
		// 새로운 사용자 이메일에 사용할 카운터
		AtomicInteger userCounter = new AtomicInteger(0);

		Queue<Integer> procedure = new LinkedList<>();

		long startTime = System.currentTimeMillis();

		for (int i = 0; i < attempts; i++) {
			executor.submit(() -> {
				try {
					// 매번 고유한 사용자 생성
					int userIdNumber = userCounter.incrementAndGet();
					String email = "user" + userIdNumber + "@example.com";
					User newUser = new User(email, "password", "User" + userIdNumber + " ", "Test", UserRole.ROLE_ADMIN);
					newUser = userRepository.save(newUser);
					AuthUser newAuthUser = new AuthUser(newUser.getId(), newUser.getEmail(), newUser.getRole());
					// 쿠폰 발급 시도 (쿠폰 stock은 내부에서 1씩 감소)
					couponService.publishCouponFair(newAuthUser, testCoupon.getId());
					successCount.incrementAndGet();
					procedure.add(userIdNumber);
				} catch (Exception e) {
					// 재고 소진이나 중복 발급 등으로 실패하면 예외가 발생할 수 있음
					System.out.println("Exception: " + e.getMessage());
				} finally {
					latch.countDown();
				}
			});
		}
		latch.await();
		executor.shutdown();

		long endTime = System.currentTimeMillis();
		long durationInMillis = endTime - startTime;
		double durationInSeconds = durationInMillis / 1000.0;

		// 최종 쿠폰 stock과 발급 건수를 확인합니다.
		Coupon finalCoupon = couponRepository.findById(testCoupon.getId()).orElse(null);
		int finalStock = finalCoupon != null ? finalCoupon.getStock() : -1;
		// testCoupon에 해당하는 쿠폰 발급 기록만 필터링
		List<CouponHistory> allHistories = couponHistoryRepository.findAll();
		long couponHistoryCount = allHistories.stream()
			.filter(ch -> ch.getCoupon().getId().equals(testCoupon.getId()))
			.count();

		System.out.println("쿠폰 발급 횟수: " + successCount.get());
		System.out.println("최종 쿠폰 수량: " + finalStock);
		System.out.println("발급받은 유저 수: " + couponHistoryCount);
		System.out.println("테스트 실행 시간: " + durationInSeconds + "초");
		System.out.println("쿠폰 발급밭은 순서: ");
		while (!procedure.isEmpty()) {
			System.out.print(" " + procedure.poll() + " ");
		}

		// 초기 쿠폰 stock이 10이라면 최대 10건만 발급되어야 합니다.
		assertEquals(0, finalStock, "모든 쿠폰이 소진되어야 합니다.");
		assertEquals(10, couponHistoryCount, "쿠폰 발급 기록은 초기 stock 10건이어야 합니다.");
	}

}

