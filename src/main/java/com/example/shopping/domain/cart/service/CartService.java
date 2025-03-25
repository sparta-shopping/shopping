package com.example.shopping.domain.cart.service;

import com.example.shopping.common.dto.PageResponseDto;
import com.example.shopping.domain.cart.dto.request.CreateCartRequestDto;
import com.example.shopping.domain.cart.dto.response.GetCartResponseDto;
import com.example.shopping.domain.product.entity.Product;
import com.example.shopping.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

import static com.example.shopping.common.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class CartService {
	
	private final RedisTemplate<String, Object> redisTemplate;
	private final ProductRepository productRepository;
	private final String CART_PREFIX = "cart:";
	
	@Transactional
	public void addCart(Long userId, Long productId, CreateCartRequestDto dto) {
		Product findProduct = getProduct(productId);
		checkStock(findProduct, dto.getQuantity());
		
		String key = CART_PREFIX + userId;
		checkQuantity(userId, productId, dto.getQuantity());
		
		redisTemplate.opsForHash().increment(key, productId.toString(), dto.getQuantity());
	}
	
	@Transactional(readOnly = true)
	public PageResponseDto<GetCartResponseDto> getCarts(
		Long userId,
		Pageable pageable
	) {
		String key = CART_PREFIX + userId;
		Map<Object, Object> cartItems = redisTemplate.opsForHash().entries(key);
		
		List<GetCartResponseDto> cartList = cartItems.entrySet().stream()
			.map(entry -> {
				Long productId = Long.parseLong(entry.getKey().toString());
				Integer quantity = Integer.parseInt(entry.getValue().toString());
				Product product = getProduct(productId);
				
				return GetCartResponseDto.of(userId, product, quantity);
			}).toList();
		
		int start = (int) pageable.getOffset();
		int end = Math.min(start + pageable.getPageSize(), cartList.size());
		List<GetCartResponseDto> pageList = cartList.subList(start, end);
		
		Page<GetCartResponseDto> page = new PageImpl<>(pageList, pageable, cartList.size());
		
		return new PageResponseDto<>(page);
	}
	
	@Transactional
	public void deleteItem(Long userId, Long productId) {
		String key = CART_PREFIX + userId;
		
		Object cartItem = redisTemplate.opsForHash().get(key, productId.toString());
		checkCartItem(productId, cartItem);
	}
	
	@Transactional
	public void deleteCart(Long userId) {
		String key = CART_PREFIX + userId;
		redisTemplate.delete(key);
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
	
	// 상품의 남은 재고보다 구입하려는 수량이 많을 때의 예외처리
	private void checkStock(Product product, Integer quantity) {
		if (product.getStock() < quantity) {
			throw new ResponseStatusException(OUT_OF_STOCK.getStatus(), OUT_OF_STOCK.getMessage());
		}
	}
	
	// 장바구니의 물건 개수가 음수가 되는 것을 방지하는 예외처리
	private void checkQuantity(Long userId, Long productId, Integer quantity) {
		String key = CART_PREFIX + userId;
		
		Object currentQuantityObj = redisTemplate.opsForHash().get(key, productId.toString());
		Integer currentQuantity = (currentQuantityObj != null)
			? Integer.parseInt(currentQuantityObj.toString())
			: 0;
		
		if (currentQuantity + quantity < 0) {
			throw new ResponseStatusException(
				QUANTITY_CAN_NOT_MINUS.getStatus(), QUANTITY_CAN_NOT_MINUS.getMessage()
			);
		}
	}
	
	// 내 카트에 해당 아이템이 있다면 해당 아이템 제거 없다면 예외처리 메서드
	private void checkCartItem(Long productId, Object cartItem) {
		if (cartItem == null) {
			throw new ResponseStatusException(PRODUCT_NOT_FOUND.getStatus(), PRODUCT_NOT_FOUND.getMessage());
		} else {
			redisTemplate.opsForHash().delete(productId.toString());
		}
	}
}
