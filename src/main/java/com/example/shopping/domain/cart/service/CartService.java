package com.example.shopping.domain.cart.service;

import com.example.shopping.domain.cart.dto.request.CreateCartRequestDto;
import com.example.shopping.domain.cart.dto.response.GetCartResponseDto;
import com.example.shopping.domain.product.entity.Product;
import com.example.shopping.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.example.shopping.common.exception.ErrorCode.OUT_OF_STOCK;
import static com.example.shopping.common.exception.ErrorCode.PRODUCT_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class CartService {
	
	private final RedisTemplate<String, Object> redisTemplate;
	private final ProductRepository productRepository;
	public static final String CART_PREFIX = "cart:";
	
	@Transactional
	public void addCart(Long userId, Long productId, CreateCartRequestDto dto) {
		Product findProduct = getProduct(productId);
		quantityPermission(userId, findProduct, dto.getQuantity());
		
		String key = CART_PREFIX + userId;
		Integer currentQuantity = getQuantity(key, productId) + dto.getQuantity();
		
		redisTemplate.opsForHash().put(key, productId.toString(), currentQuantity);
		redisTemplate.expire(key, 10, TimeUnit.HOURS);
	}
	
	@Transactional(readOnly = true)
	public List<GetCartResponseDto> getCarts(
		Long userId
	) {
		String key = CART_PREFIX + userId;
		Map<Object, Object> cartItems = redisTemplate.opsForHash().entries(key);
		
		return cartItems.entrySet().stream()
			.map(entry -> {
				Long productId = Long.parseLong(entry.getKey().toString());
				Integer quantity = Integer.parseInt(entry.getValue().toString());
				Product product = getProduct(productId);
				
				return GetCartResponseDto.of(userId, product, quantity);
			}).toList();
	}
	
	@Transactional
	public void deleteItem(Long userId, Long productId) {
		String key = CART_PREFIX + userId;
		
		Object cartItem = redisTemplate.opsForHash().get(key, productId.toString());
		if (cartItem == null) {
			throw new ResponseStatusException(PRODUCT_NOT_FOUND.getStatus(), PRODUCT_NOT_FOUND.getMessage());
		}
		
		redisTemplate.opsForHash().delete(key, productId.toString());
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
	
	// 장바구니의 물건 개수가 음수가 되거나, 해당 제품의 재고를 넘어갈 때의 예외처리
	private void quantityPermission(Long userId, Product product, Integer quantity) {
		String key = CART_PREFIX + userId;
		Integer currentQuantity = getQuantity(key, product.getId()) + quantity;
		
		if (currentQuantity + quantity < 0 || product.getStock() < currentQuantity) {
			throw new ResponseStatusException(
				OUT_OF_STOCK.getStatus(), OUT_OF_STOCK.getMessage()
			);
		}
	}
	
	// 현재 장바구니 내 해당 상품의 개수를 반환하는 메서드
	private Integer getQuantity(String key, Long productId) {
		Object currentQuantityObj = redisTemplate.opsForHash().get(key, productId.toString());
		return (currentQuantityObj != null)
			? Integer.parseInt(currentQuantityObj.toString())
			: 0;
	}
}
