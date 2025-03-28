package com.example.shopping.domain.cart.service;

import com.example.shopping.domain.cart.dto.request.CreateCartRequestDto;
import com.example.shopping.domain.cart.dto.response.GetCartResponseDto;
import com.example.shopping.domain.product.category.Category;
import com.example.shopping.domain.product.dto.request.ProductCreateRequestDto;
import com.example.shopping.domain.product.entity.Product;
import com.example.shopping.domain.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {
	
	@Mock
	private RedisTemplate<String, Object> redisTemplate;
	
	@Mock
	private HashOperations<String, Object, Object> hashOperations;
	
	@Mock
	private ProductRepository productRepository;
	
	@InjectMocks
	private CartService cartService;
	
	@BeforeEach
	void setUp() {
		when(redisTemplate.opsForHash()).thenReturn(hashOperations);
	}
	
	@Test
	void 카트를_생성한다() {
		// given
		Long userId = 1L;
		Long productId = 101L;
		Integer quantity = 2;

		ProductCreateRequestDto dto = new ProductCreateRequestDto("a", Category.PANTS, 10000, 10);
		Product product = new Product(dto.getName(), dto.getCategory(), dto.getPrice(), dto.getStock(), "a");
		ReflectionTestUtils.setField(product, "id", 1L);
		CreateCartRequestDto dto2 = new CreateCartRequestDto(quantity);
		
		// when
		when(productRepository.findProductById(productId)).thenReturn(Optional.of(product));
		when(redisTemplate.opsForHash()).thenReturn(hashOperations);
		
		cartService.addCart(userId, productId, dto2);
		
		// then
		verify(hashOperations).increment(eq("cart:" + userId), eq(productId.toString()), eq(2L));
	}
	
	@Test
	void 카트를_조회한다() {
		// given
		Long userId = 1L;
		String cartKey = "cart:" + userId;
		
		Map<Object, Object> cartItems = new HashMap<>();
		cartItems.put("101", 2);
		cartItems.put("102", 3);
		
		when(redisTemplate.opsForHash()).thenReturn(hashOperations);
		when(hashOperations.entries(cartKey)).thenReturn(cartItems);
		
		Product product1 = new Product("Product 1", Category.PANTS, 1000, 10, "product1.jpg");
		ReflectionTestUtils.setField(product1, "id", 101L);
		Product product2 = new Product("Product 2", Category.PANTS, 2000, 5, "product2.jpg");
		ReflectionTestUtils.setField(product2, "id", 102L);
		
		when(productRepository.findProductById(101L)).thenReturn(Optional.of(product1));
		when(productRepository.findProductById(102L)).thenReturn(Optional.of(product2));
		
		// when
		List<GetCartResponseDto> response = cartService.getCarts(userId);
		
		// then
		assertNotNull(response);
		assertEquals(2, response.size());
		
		GetCartResponseDto firstCartItem = response.get(0);
		assertEquals(101L, firstCartItem.getProductId());
		assertEquals(2, firstCartItem.getQuantity());
		assertEquals(2000, firstCartItem.getTotalPrice());
		
		GetCartResponseDto secondCartItem = response.get(1);
		assertEquals(102L, secondCartItem.getProductId());
		assertEquals(3, secondCartItem.getQuantity());
		assertEquals(6000, secondCartItem.getTotalPrice());
		
		verify(hashOperations).entries(cartKey);
		verify(productRepository).findProductById(101L);
		verify(productRepository).findProductById(102L);
	}
	
	@Test
	void 카트에서_아이템을_삭제한다() {
		// given
		Long userId = 1L;
		Long productId = 101L;
		String key = "cart:" + userId;
		
		when(redisTemplate.opsForHash()).thenReturn(hashOperations);
		when(hashOperations.get(key, productId.toString())).thenReturn(2);
		
		// when
		cartService.deleteItem(userId, productId);
		
		// then
		verify(hashOperations, times(1)).get(key, productId.toString());
	}
	
	@Test
	void 카트에서_해당_아이템_없을_때_삭제시_예외처리() {
		// given
		Long userId = 1L;
		Long productId = 101L;
		String key = "cart:" + userId;
		
		when(redisTemplate.opsForHash()).thenReturn(hashOperations);
		when(hashOperations.get(key, productId.toString())).thenReturn(null);
		
		// when
		Exception exception = assertThrows(ResponseStatusException.class,
			() -> {
				cartService.deleteItem(userId, productId);
			});
		String expectedMessage = "해당 상품을 찾을 수 없습니다.";
		String actualMessage = exception.getMessage();
		
		// then
		assertTrue(actualMessage.contains(expectedMessage));
	}
}
