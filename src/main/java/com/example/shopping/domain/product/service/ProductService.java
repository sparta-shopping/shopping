package com.example.shopping.domain.product.service;

import static com.example.shopping.common.exception.ErrorCode.*;

import com.example.shopping.common.dto.AuthUser;
import com.example.shopping.common.dto.PageResponseDto;
import com.example.shopping.domain.product.category.Category;
import com.example.shopping.domain.product.dto.request.ProductRequestDto;
import com.example.shopping.domain.product.dto.response.ProductResponseDto;
import com.example.shopping.domain.product.entity.Product;
import com.example.shopping.domain.product.entity.ProductUser;
import com.example.shopping.domain.product.repository.ProductRepository;
import com.example.shopping.domain.product.repository.ProductUserRepository;
import com.example.shopping.domain.user.entity.User;
import com.example.shopping.domain.user.repository.UserRepository;
import com.example.shopping.domain.user.role.UserRole;

import jakarta.persistence.Table;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class ProductService {

	private final ProductRepository productRepository;
	private final UserRepository userRepository;
	private final ProductUserRepository productUserRepository;

	@Transactional
	public ProductResponseDto createProduct(AuthUser authUser, ProductRequestDto dto) {
		User userById = getUser(authUser);

		checkAuthority(userById);

		String imageUrl = "image hi";

		Product product = new Product(dto.getName(), dto.getCategory(), dto.getPrice(), dto.getStock(), imageUrl);

		Product saveProduct = productRepository.save(product);

		ProductUser productUser = new ProductUser(saveProduct, userById);

		productUserRepository.save(productUser);

		return ProductResponseDto.of(saveProduct);
	}

	@Transactional(readOnly = true)
	public ProductResponseDto findProduct(Long productId) {
		Product product = getProduct(productId);

		return ProductResponseDto.of(product);
	}

	@Transactional(readOnly = true)
	public PageResponseDto<ProductResponseDto> findProducts(Category category, String keyword, Pageable pageable) {
		Page<ProductResponseDto> products = productRepository.findProductsByCategoryAndKeyword(
			category, keyword, pageable
		);

		return new PageResponseDto<>(products);
	}

	@Transactional
	public ProductResponseDto updateProduct(AuthUser authUser, Long productId, ProductRequestDto dto) {
		User userById = getUser(authUser);

		checkAuthority(userById);

		Product product = getProduct(productId);

		product.updateProduct(dto);

		ProductUser productUser = new ProductUser(product, userById);

		productUserRepository.save(productUser);

		return ProductResponseDto.of(product);
	}

	@Transactional
	public void deleteProduct(AuthUser authUser, Long productId) {
		User userById = getUser(authUser);

		checkAuthority(userById);

		Product product = getProduct(productId);

		product.setDeletedAt();

		ProductUser productUser = new ProductUser(product, userById);

		productUserRepository.save(productUser);
	}

	@Transactional
	public ProductResponseDto restoreProduct(AuthUser authUser, Long productId, ProductRequestDto dto) {
		User userById = getUser(authUser);

		checkAuthority(userById);

		Product product = getProduct(productId);

		product.updateProduct(dto);

		product.restoreDeletedAt();

		ProductUser productUser = new ProductUser(product, userById);

		productUserRepository.save(productUser);

		return ProductResponseDto.of(product);
	}

	private User getUser(AuthUser authUser) {
		return userRepository.findUserById(authUser.getId())
			.orElseThrow(() -> new ResponseStatusException(USER_NOT_FOUND.getStatus(), USER_NOT_FOUND.getMessage()));
	}

	private void checkAuthority(User userById) {
		if (userById.getRole() != UserRole.ROLE_ADMIN) {
			throw new ResponseStatusException(USER_ACCESS_DENIED.getStatus(), USER_ACCESS_DENIED.getMessage());
		}
	}

	private Product getProduct(Long productId) {
		return productRepository.findProductById(productId)
			.orElseThrow(
				() -> new ResponseStatusException(PRODUCT_NOT_FOUND.getStatus(), PRODUCT_NOT_FOUND.getMessage()));
	}
}
