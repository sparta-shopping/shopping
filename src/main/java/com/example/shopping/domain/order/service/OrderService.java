package com.example.shopping.domain.order.service;

import com.example.shopping.common.dto.PageResponseDto;
import com.example.shopping.domain.cart.dto.response.GetCartResponseDto;
import com.example.shopping.domain.cart.service.CartService;
import com.example.shopping.domain.coupon.entity.Coupon;
import com.example.shopping.domain.coupon.repository.CouponRepository;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static com.example.shopping.common.exception.ErrorCode.*;
import static com.example.shopping.domain.order.state.OrderState.*;
import static com.example.shopping.domain.user.role.UserRole.ROLE_ADMIN;

@Service
@RequiredArgsConstructor
public class OrderService {
	
	private final OrderRepository orderRepository;
	private final UserRepository userRepository;
	private final ProductRepository productRepository;
	private final CartService cartService;
	private final CouponRepository couponRepository;
	
	@Transactional
	public CreateOrderResponseDto saveOrder(Long userId, CreateOrderRequestDto dto) {
		User user = getUser(userId);
		List<GetCartResponseDto> cartItems = cartService.getCarts(userId);
		
		int totalPrice = 0;
		Order order = new Order(
			PENDING,
			totalPrice,
			user,
			null
		);
		
		for(GetCartResponseDto cartItem : cartItems) {
			Product product = getProduct(cartItem.getProductId());
			
			if(product.getStock() < cartItem.getQuantity()) {
				throw new ResponseStatusException(
					OUT_OF_STOCK.getStatus(), OUT_OF_STOCK.getMessage()
				);
			}
			
			int price = product.getPrice() * cartItem.getQuantity();
			totalPrice += price;
			
			OrderItem orderItem = new OrderItem(
				product.getId(),
				cartItem.getQuantity(),
				price
			);
			order.addOrderItem(orderItem);
		}
		
		if(dto.getCouponId() != null) {
			Coupon coupon = couponRepository.findCouponById(dto.getCouponId())
				.orElse(null);
			
			if(coupon != null) {
				totalPrice -= coupon.getDiscountAmount();
				order.setCoupon(coupon);
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
	
	private User getUser(Long userId) {
		return userRepository.findUserById(userId)
			.orElseThrow(
				() -> new ResponseStatusException(
					USER_NOT_FOUND.getStatus(), USER_NOT_FOUND.getMessage()
				)
			);
	}
	
	private Order getOrder(Long orderId) {
		return orderRepository.findOrderById(orderId)
			.orElseThrow(
				() -> new ResponseStatusException(
					ORDER_NOT_FOUND.getStatus(), ORDER_NOT_FOUND.getMessage()
				)
			);
	}
	
	private void checkOrderUserPermission(User user, Order order) {
		if(!order.getUser().equals(user)) {
			throw new ResponseStatusException(
				USER_ACCESS_DENIED.getStatus(), USER_ACCESS_DENIED.getMessage()
			);
		}
	}
	
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
