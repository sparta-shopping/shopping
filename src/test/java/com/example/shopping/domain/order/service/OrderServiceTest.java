package com.example.shopping.domain.order.service;

import com.example.shopping.common.dto.AuthUser;
import com.example.shopping.common.util.JwtUtil;
import com.example.shopping.domain.cart.dto.request.CreateCartRequestDto;
import com.example.shopping.domain.cart.service.CartService;
import com.example.shopping.domain.order.dto.request.CreateOrderRequestDto;
import com.example.shopping.domain.order.repository.OrderRepository;
import com.example.shopping.domain.product.category.Category;
import com.example.shopping.domain.product.entity.Product;
import com.example.shopping.domain.product.repository.ProductRepository;
import com.example.shopping.domain.user.entity.User;
import com.example.shopping.domain.user.repository.UserRepository;
import com.example.shopping.domain.user.role.UserRole;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootTest
class OrderServiceTest {
	
	@MockitoBean
	private JwtUtil jwtUtil;
	
	@Autowired
	private OrderServiceConcurrency orderServiceConcurrency;
	
	@Autowired
	private ProductRepository productRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private OrderRepository orderRepository;
	
	@Autowired
	private CartService cartService;
	
	private final int TOTAL_USERS = 100;
	private final Long PRODUCT_ID = 1L;
	private Integer INITIAL_STOCK = 10;
	
	private AuthUser testAuthUser;
	private User testUser;
	
	@BeforeEach
	public void setUp() {
		userRepository.deleteAll();
		productRepository.deleteAll();
		orderRepository.deleteAll();
		
		Product product = new Product("Test Product", Category.PANTS, 1000, INITIAL_STOCK, "a.jpg");
		productRepository.save(product);
		
		// 테스트용 User 생성 (테스트 환경에 맞게 수정)
		testUser = new User("testuser@example.com", "password", "Test User", "Test", UserRole.ROLE_ADMIN);
		testUser = userRepository.save(testUser);
		testAuthUser = new AuthUser(testUser.getId(), testUser.getEmail(), testUser.getRole());
	}
	
	// AOP를 활용한 분산락
	@Test
	public void testConcurrentOrderRequests() throws InterruptedException {
		ExecutorService executorService = Executors.newFixedThreadPool(30);
		CountDownLatch latch = new CountDownLatch(TOTAL_USERS);
		CreateCartRequestDto dto = new CreateCartRequestDto(1);
		CreateOrderRequestDto requestDto = new CreateOrderRequestDto(null);
		AtomicInteger userCounter = new AtomicInteger(0);
		AtomicInteger successCount = new AtomicInteger(0);
		
		long startTime = System.currentTimeMillis();
		
		for (long userId = 1; userId <= TOTAL_USERS; userId++) {
			executorService.submit(() -> {
				try {
					int finalUserId = userCounter.incrementAndGet();
					String email = "user" + finalUserId + "@example.com";
					User newUser = new User(email, "password", "User " + finalUserId, "Test", UserRole.ROLE_USER);
					
					newUser = userRepository.save(newUser);
					AuthUser newAuthUser = new AuthUser(newUser.getId(), newUser.getEmail(), newUser.getRole());
					
					cartService.addCart(newAuthUser.getId(), PRODUCT_ID, dto);
					orderServiceConcurrency.saveOrderV2(newAuthUser.getId(), requestDto);
					successCount.getAndIncrement();
					System.out.println("현재 주문 수 : " + successCount);
				} catch (Exception e) {
					System.out.println("Order failed: " + e.getMessage());
				} finally {
					latch.countDown();
				}
			});
		}
		
		latch.await();
		executorService.shutdown();
		
		long endTime = System.currentTimeMillis();
		long durationInMillis = endTime - startTime;
		double durationInSeconds = durationInMillis / 1000.0;
		int orderCount = Integer.parseInt(successCount.toString());
		
		System.out.println("걸린 시간 : " + durationInSeconds);
		System.out.println("주문 수 : " + orderCount);
		System.out.println("레포지토리 : " + orderRepository.count());
		
		// 최종적으로 남은 재고 확인
		Product product = productRepository.findById(PRODUCT_ID).orElse(null);
		if (product != null) {
			System.out.println("남은 재고 : " + product.getStock());
			Assertions.assertEquals(0, product.getStock());
		}
		
		// 주문이 정확하게 생성되었는지 확인
		Assertions.assertEquals(INITIAL_STOCK, orderCount);
	}
}
