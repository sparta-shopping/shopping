package com.example.shopping.domain.product.service;

import com.example.shopping.common.dto.AuthUser;
import com.example.shopping.domain.product.category.Category;
import com.example.shopping.domain.product.dto.request.ProductCreateRequestDto;
import com.example.shopping.domain.product.dto.request.ProductUpdateRequestDto;
import com.example.shopping.domain.product.dto.response.ProductResponseDto;
import com.example.shopping.domain.product.entity.Product;
import com.example.shopping.domain.product.repository.ProductRepository;
import com.example.shopping.domain.product.repository.ProductTouchMDRepository;
import com.example.shopping.domain.user.entity.User;
import com.example.shopping.domain.user.repository.UserRepository;
import com.example.shopping.domain.user.role.UserRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

	@Mock
	private ProductRepository productRepository;
	@Mock
	private ProductTouchMDRepository productTouchMDRepository;
	@Mock
	private UserRepository userRepository;
	@InjectMocks
	private ProductService productService;

	@Test
	void 제품_생성_성공(){
		// given
		AuthUser authUser = new AuthUser(1L, "a@a.com", UserRole.ROLE_USER);
		User user = new User("a@a.com", "1", "a", "1a", UserRole.ROLE_ADMIN);
		ProductCreateRequestDto dto = new ProductCreateRequestDto("a", Category.PANTS, 10000, 10);
		Product product = new Product(dto.getName(), dto.getCategory(), dto.getPrice(), dto.getStock(), "a");
		ReflectionTestUtils.setField(product, "id", 1L);
		when(userRepository.findUserById(authUser.getId())).thenReturn(Optional.of(user));
		when(productRepository.save(any(Product.class))).thenReturn(product);

		// when
		ProductResponseDto result = productService.createProduct(authUser, dto);

		// then
		assertNotNull(result);
		assertEquals(result.getName(), dto.getName());
	}

	@Test
	void 제품_되살리기_성공(){
		// given
		AuthUser authUser = new AuthUser(1L, "a@a.com", UserRole.ROLE_USER);
		Long productId = 1L;
		User user = new User("a@a.com", "1", "a", "1a", UserRole.ROLE_ADMIN);
		ProductUpdateRequestDto dto = new ProductUpdateRequestDto("a", Category.PANTS, 10000, 10);
		Product product = new Product(dto.getName(), dto.getCategory(), dto.getPrice(), dto.getStock(), "a");
		ReflectionTestUtils.setField(product, "id", 1L);
		ReflectionTestUtils.setField(product, "deletedAt", LocalDateTime.now());
		when(userRepository.findUserById(authUser.getId())).thenReturn(Optional.of(user));
		when(productRepository.findProductById(any(Long.class))).thenReturn(Optional.of(product));

		// when
		ProductResponseDto result = productService.restoreProduct(authUser, productId, dto);

		// then
		assertNull(result.getDeletedAt());
	}
}