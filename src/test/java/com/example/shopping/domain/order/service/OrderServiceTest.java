package com.example.shopping.domain.order.service;

import com.example.shopping.domain.cart.dto.response.GetCartResponseDto;
import com.example.shopping.domain.cart.service.CartService;
import com.example.shopping.domain.coupon.entity.Coupon;
import com.example.shopping.domain.coupon.repository.CouponRepository;
import com.example.shopping.domain.order.dto.request.CreateOrderRequestDto;
import com.example.shopping.domain.order.dto.response.CreateOrderResponseDto;
import com.example.shopping.domain.order.entity.Order;
import com.example.shopping.domain.order.repository.OrderRepository;
import com.example.shopping.domain.product.category.Category;
import com.example.shopping.domain.product.entity.Product;
import com.example.shopping.domain.product.repository.ProductRepository;
import com.example.shopping.domain.user.entity.User;
import com.example.shopping.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OrderServiceTest {

	@InjectMocks
	private OrderService orderService;

	@Mock
	private OrderRepository orderRepository;

	@Mock
	private CartService cartService;

	@Mock
	private ProductRepository productRepository;

	@Mock
	private CouponRepository couponRepository;

	@Mock
	private UserRepository userRepository;

	private Long userId;
	private CreateOrderRequestDto createOrderRequestDto;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		userId = 1L;
		createOrderRequestDto = new CreateOrderRequestDto(1L);
	}

	@Test
	void 쿠폰이_있는_주문을_저장한다() {
		// Given
		User user = new User();
		ReflectionTestUtils.setField(user, "id", userId);

		Product product = new Product("Test Product", Category.PANTS, 100, 10, "Description");
		ReflectionTestUtils.setField(product, "id", 1L);

		GetCartResponseDto cartItem = GetCartResponseDto.of(userId, product, 2);
		List<GetCartResponseDto> cartItems = List.of(cartItem);

		Coupon coupon = new Coupon();
		ReflectionTestUtils.setField(coupon, "id", 1L);
		ReflectionTestUtils.setField(coupon, "discountAmount", 50);

		when(userRepository.findUserById(userId)).thenReturn(Optional.of(user));
		when(cartService.getCarts(userId)).thenReturn(cartItems);
		when(productRepository.findProductById(1L)).thenReturn(Optional.of(product));
		when(couponRepository.findCouponById(1L)).thenReturn(Optional.of(coupon));

		// When
		CreateOrderResponseDto response = orderService.saveOrder(userId, createOrderRequestDto);

		// Then
		verify(cartService).deleteCart(userId);
		verify(orderRepository).save(any(Order.class));

		assertEquals(1, response.getOrderItems().size());
		assertEquals(1L, response.getOrderItems().get(0).getProductId());
		assertEquals(2, response.getOrderItems().get(0).getQuantity());
		assertEquals(150, response.getTotalPrice());
	}

	@Test
	void 쿠폰이_없는_주문을_저장한다() {
		// Given
		User user = new User();
		ReflectionTestUtils.setField(user, "id", userId);

		Product product = new Product("Test Product", Category.PANTS, 100, 10, "Description");
		ReflectionTestUtils.setField(product, "id", 1L);

		GetCartResponseDto cartItem = GetCartResponseDto.of(userId, product, 3);
		List<GetCartResponseDto> cartItems = List.of(cartItem);

		when(userRepository.findUserById(userId)).thenReturn(Optional.of(user));
		when(cartService.getCarts(userId)).thenReturn(cartItems);
		when(productRepository.findProductById(1L)).thenReturn(Optional.of(product));
		when(couponRepository.findCouponById(any())).thenReturn(Optional.empty());

		// When
		CreateOrderResponseDto response = orderService.saveOrder(userId, new CreateOrderRequestDto(null));

		// Then
		verify(cartService).deleteCart(userId);
		verify(orderRepository).save(any(Order.class));

		assertEquals(1, response.getOrderItems().size());
		assertEquals(1L, response.getOrderItems().get(0).getProductId());
		assertEquals(3, response.getOrderItems().get(0).getQuantity());
		assertEquals(300, response.getTotalPrice());
	}
}
