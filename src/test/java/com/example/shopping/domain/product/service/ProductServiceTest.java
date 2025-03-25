package com.example.shopping.domain.product.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import org.springframework.test.util.ReflectionTestUtils;

import com.example.shopping.config.JpaTestConfig;
import com.example.shopping.domain.product.category.Category;
import com.example.shopping.domain.product.dto.request.ProductRequestDto;
import com.example.shopping.domain.product.dto.response.ProductResponseDto;
import com.example.shopping.domain.product.entity.Product;
import com.example.shopping.domain.product.repository.ProductRepository;
import com.example.shopping.domain.product.repository.ProductUserRepository;
import com.example.shopping.domain.user.entity.User;
import com.example.shopping.domain.user.repository.UserRepository;
import com.example.shopping.domain.user.role.UserRole;

@Import(JpaTestConfig.class)
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

	@Mock
	private ProductRepository productRepository;
	@Mock
	private ProductUserRepository productUserRepository;
	@Mock
	private UserRepository userRepository;
	@InjectMocks
	private ProductService productService;

	@Test
	void 제품_생성_성공(){
		// given
		User user = new User("a@a.com", "1", "a", "1a", UserRole.ADMIN);
		ProductRequestDto dto = new ProductRequestDto("a", Category.PANTS, 10000, 10);
		Product product = new Product(dto.getName(), dto.getCategory(), dto.getPrice(), dto.getStock(), "a");
		ReflectionTestUtils.setField(product, "id", 1L);
		when(userRepository.findUserById(user.getId())).thenReturn(Optional.of(user));
		when(productRepository.save(any(Product.class))).thenReturn(product);

		// when
		ProductResponseDto result = productService.createProduct(user, dto);

		// then
		assertNotNull(result);
		assertEquals(result.getName(), dto.getName());
	}

}