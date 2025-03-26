package com.example.shopping.domain.product.service;

import static com.example.shopping.common.exception.ErrorCode.*;

import com.example.shopping.common.dto.AuthUser;
import com.example.shopping.common.dto.PageResponseDto;
import com.example.shopping.domain.product.category.Category;
import com.example.shopping.domain.product.dto.request.ProductCreateRequestDto;
import com.example.shopping.domain.product.dto.request.ProductUpdateRequestDto;
import com.example.shopping.domain.product.dto.response.ProductResponseDto;
import com.example.shopping.domain.product.entity.Product;
import com.example.shopping.domain.product.entity.ProductTouchMD;
import com.example.shopping.domain.product.repository.ProductRepository;
import com.example.shopping.domain.product.repository.ProductTouchMDRepository;
import com.example.shopping.domain.user.entity.User;
import com.example.shopping.domain.user.repository.UserRepository;
import com.example.shopping.domain.user.role.UserRole;

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
	private final ProductTouchMDRepository productTouchMDRepository;

	@Transactional
	public ProductResponseDto createProduct(AuthUser authUser, ProductCreateRequestDto dto) {
		User userById = getUser(authUser);

		checkAuthority(userById);

		String imageUrl = "image hi";

		Product product = new Product(dto.getName(), dto.getCategory(), dto.getPrice(), dto.getStock(), imageUrl);

		Product saveProduct = productRepository.save(product);

		ProductTouchMD productTouchMD = new ProductTouchMD(saveProduct, userById);

		productTouchMDRepository.save(productTouchMD);

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
	public ProductResponseDto updateProduct(AuthUser authUser, Long productId, ProductUpdateRequestDto dto) {
		User userById = getUser(authUser);

		checkAuthority(userById);

		Product product = getProduct(productId);

		product.updateProduct(dto);

		ProductTouchMD productTouchMD = new ProductTouchMD(product, userById);

		productTouchMDRepository.save(productTouchMD);

		return ProductResponseDto.of(product);
	}

	@Transactional
	public void deleteProduct(AuthUser authUser, Long productId) {
		User userById = getUser(authUser);

		checkAuthority(userById);

		Product product = getProduct(productId);

		product.setDeletedAt();

		ProductTouchMD productTouchMD = new ProductTouchMD(product, userById);

		productTouchMDRepository.save(productTouchMD);
	}

	@Transactional
	public ProductResponseDto restoreProduct(AuthUser authUser, Long productId, ProductUpdateRequestDto dto) {
		User userById = getUser(authUser);

		checkAuthority(userById);

		Product product = getProduct(productId);

		product.updateProduct(dto);

		product.restoreDeletedAt();

		ProductTouchMD productTouchMD = new ProductTouchMD(product, userById);

		productTouchMDRepository.save(productTouchMD);

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
