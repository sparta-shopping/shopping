package com.example.shopping.domain.product.service;

import static com.example.shopping.common.exception.ErrorCode.*;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.shopping.common.dto.AuthUser;
import com.example.shopping.common.dto.PageResponseDto;
import com.example.shopping.domain.product.dto.response.ProductTouchMDResponseDto;
import com.example.shopping.domain.product.entity.Product;
import com.example.shopping.domain.product.repository.ProductRepository;
import com.example.shopping.domain.product.repository.ProductTouchMDRepository;
import com.example.shopping.domain.user.entity.User;
import com.example.shopping.domain.user.repository.UserRepository;
import com.example.shopping.domain.user.role.UserRole;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductUserService {

	private final ProductTouchMDRepository productTouchMDRepository;
	private final UserRepository userRepository;
	private final ProductRepository productRepository;

	@Transactional(readOnly = true)
	public PageResponseDto<ProductTouchMDResponseDto> chaseMD(AuthUser authUser, Long productId, Pageable pageable) {
		User userById = getUser(authUser);

		checkAuthority(userById);

		Product product = getProduct(productId);

		List<ProductTouchMDResponseDto> MDs = productTouchMDRepository.findProductUsersByProduct(product)
			.stream()
			.map(ProductTouchMDResponseDto::of)
			.toList();

		Page<ProductTouchMDResponseDto> productUsers = new PageImpl<>(MDs, pageable, MDs.size());

		return new PageResponseDto<>(productUsers);
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
		return productRepository.findById(productId)
			.orElseThrow(
				() -> new ResponseStatusException(PRODUCT_NOT_FOUND.getStatus(), PRODUCT_NOT_FOUND.getMessage()));
	}
}

