package com.example.shopping.domain.product.service;

import static com.example.shopping.common.exception.ErrorCode.*;

import java.util.Optional;

import com.example.shopping.common.dto.PageResponseDto;
import com.example.shopping.domain.product.category.Category;
import com.example.shopping.domain.product.dto.request.ProductRequestDto;
import com.example.shopping.domain.product.dto.response.ProductResponseDto;
import com.example.shopping.domain.product.entity.Product;
import com.example.shopping.domain.product.repository.ProductRepository;
import com.example.shopping.domain.user.entity.User;
import com.example.shopping.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class ProductService {

	private final ProductRepository productRepository;
	private final UserRepository userRepository;

	public ProductResponseDto createProduct(User user, ProductRequestDto dto) {
		User userById = userRepository.findUserById(user.getId())
			.orElseThrow(() -> new ResponseStatusException(USER_NOT_FOUND.getStatus(), USER_NOT_FOUND.getMessage()));

		String imageUrl = "image hi";

		Product product = new Product(dto.getName(), dto.getCategory(), dto.getPrice(), dto.getStock(), imageUrl, user);

		Product saveProduct = productRepository.save(product);

		return ProductResponseDto.of(saveProduct);
	}

	public ProductResponseDto findProduct(Long productId) {
		Product product = productRepository.findProductById(productId)
			.orElseThrow(
				() -> new ResponseStatusException(PRODUCT_NOT_FOUND.getStatus(), PRODUCT_NOT_FOUND.getMessage()));

		return ProductResponseDto.of(product);
	}

	public PageResponseDto<ProductResponseDto> findProducts(Category category, String keyword, Pageable pageable) {
		Page<ProductResponseDto> products = productRepository.findProductsByCategoryAndKeyword(
			category, keyword, pageable
		);
		return new PageResponseDto<>(products);
	}
}
