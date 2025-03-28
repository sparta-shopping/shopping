package com.example.shopping.domain.order.service;

import com.example.shopping.common.dto.PageResponseDto;
import com.example.shopping.domain.cart.service.CartService;
import com.example.shopping.domain.coupon.entity.Coupon;
import com.example.shopping.domain.coupon.repository.CouponRepository;
import com.example.shopping.domain.coupon.service.CouponService;
import com.example.shopping.domain.order.dto.request.CreateOrderRequestDto;
import com.example.shopping.domain.order.dto.response.CreateOrderResponseDto;
import com.example.shopping.domain.order.dto.response.GetOrderResponseDto;
import com.example.shopping.domain.order.dto.response.GetOrdersResponseDto;
import com.example.shopping.domain.order.dto.response.UpdateOrderResponseDto;
import com.example.shopping.domain.order.entity.Order;
import com.example.shopping.domain.order.entity.OrderItem;
import com.example.shopping.domain.order.repository.OrderRepository;
import com.example.shopping.domain.order.state.OrderState;
import com.example.shopping.domain.product.entity.Product;
import com.example.shopping.domain.product.repository.ProductRepository;
import com.example.shopping.domain.user.entity.User;
import com.example.shopping.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

import static com.example.shopping.common.exception.ErrorCode.*;
import static com.example.shopping.domain.order.state.OrderState.*;
import static com.example.shopping.domain.user.role.UserRole.ROLE_ADMIN;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
	
	private final OrderRepository orderRepository;
	private final UserRepository userRepository;
	private final ProductRepository productRepository;
	private final CartService cartService;
	private final CouponRepository couponRepository;
	private final RedisTemplate<String, Object> redisTemplate;
	private final CouponService couponService;
	
	@Transactional
	public CreateOrderResponseDto saveOrder(Long userId, CreateOrderRequestDto dto) {
		User user = getUser(userId);
		
		String cartKey = cartService.getKey(userId);
		Map<Object, Object> cartItems = redisTemplate.opsForHash().entries(cartKey);
		
		List<OrderItem> orderItems = cartItems.entrySet().stream()
			.map(entry -> {
				Long productId = Long.parseLong(entry.getKey().toString());
				Integer quantity = Integer.parseInt(entry.getValue().toString());
				Product product = getProduct(productId);
				
				if(product.getStock() < quantity) {
					throw new ResponseStatusException(
						OUT_OF_STOCK.getStatus(), OUT_OF_STOCK.getMessage()
					);
				}
				log.info("Product {} stock before decrease: {}", product.getId(), product.getStock());
				product.decreaseStock(quantity);
				log.info("Product {} stock after decrease: {}", product.getId(), product.getStock());
				return new OrderItem(productId, quantity, quantity * product.getPrice());
		}).toList();
		
		Integer totalPrice = 0;
		Order order = new Order(
			PENDING,
			totalPrice,
			user,
			null
		);
		
		for (OrderItem orderItem : orderItems) {
			totalPrice += orderItem.getPrice();
			order.addOrderItem(orderItem);
		}
		
		if(dto.getCouponId() != null) {
			Coupon coupon = couponRepository.findCouponById(dto.getCouponId())
				.orElse(null);
			if (coupon != null && coupon.getUser().getId().equals(order.getUser().getId())) {
				totalPrice -= coupon.getDiscountAmount();
				order.setCoupon(coupon);
				couponService.useCoupon(coupon.getId(), user);
			}
		}
		order.setTotalPrice(totalPrice);
		
		cartService.deleteCart(userId);
		
		orderRepository.save(order);
		
		return CreateOrderResponseDto.of(order);
	}
	
	@Transactional(readOnly = true)
	public GetOrderResponseDto getOrder(Long userId, Long orderId) {
		User user = getUser(userId);
		Order order = getOrder(orderId);
		
		checkOrderUserPermission(user, order);
		
		return GetOrderResponseDto.of(order);
	}
	
	@Transactional(readOnly = true)
	public PageResponseDto<GetOrdersResponseDto> getOrders(Long userId, Pageable pageable) {
		Page<Order> orders = orderRepository.findAllByUserId(userId, pageable);
		return new PageResponseDto<>(orders.map(GetOrdersResponseDto::of));
	}
	
	@Transactional
	public UpdateOrderResponseDto updateOrder(Long userId, Long orderId) {
		User user = getUser(userId);
		Order order = getOrder(orderId);
		
		if (order.getState().equals(PENDING)) {
			checkUserRolePermission(user, order, DELIVERING);
		} else if (order.getState().equals(DELIVERING)) {
			checkUserRolePermission(user, order, FINISH);
		} else {
			throw new ResponseStatusException(
				ORDER_ALREADY_FINISH.getStatus(), ORDER_ALREADY_FINISH.getMessage()
			);
		}
		
		return UpdateOrderResponseDto.of(order);
	}
	
	// 상품의 아이디를 통해 상품을 가져오는 메서드
	private Product getProduct(Long productId) {
		return productRepository.findProductById(productId)
			.orElseThrow(
				() -> new ResponseStatusException(
					PRODUCT_NOT_FOUND.getStatus(), PRODUCT_NOT_FOUND.getMessage()
				)
			);
	}
	
	// 유저의 아이디를 통해 유저를 가져오는 메서드
	private User getUser(Long userId) {
		return userRepository.findUserById(userId)
			.orElseThrow(
				() -> new ResponseStatusException(
					USER_NOT_FOUND.getStatus(), USER_NOT_FOUND.getMessage()
				)
			);
	}
	
	// 주문의 아이디를 통해 주문을 가져오는 메서드
	private Order getOrder(Long orderId) {
		return orderRepository.findOrderById(orderId)
			.orElseThrow(
				() -> new ResponseStatusException(
					ORDER_NOT_FOUND.getStatus(), ORDER_NOT_FOUND.getMessage()
				)
			);
	}
	
	// 해당 유저와 주문한 유저가 같은지 확인 후 다르면 예외처리 
	private void checkOrderUserPermission(User user, Order order) {
		if(!order.getUser().equals(user)) {
			throw new ResponseStatusException(
				USER_ACCESS_DENIED.getStatus(), USER_ACCESS_DENIED.getMessage()
			);
		}
	}
	
	// 해당 유저의 역할이 관리자인지 확인하고 맞다면 주문 상태 변경, 아니면 예외처리
	private void checkUserRolePermission(User user, Order order, OrderState orderState) {
		if (user.getRole().equals(ROLE_ADMIN)) {
			order.setState(orderState);
		} else {
			throw new ResponseStatusException(
				USER_ACCESS_DENIED.getStatus(), USER_ACCESS_DENIED.getMessage()
			);
		}
	}
}
