package com.example.shopping.domain.product.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.example.shopping.common.dto.PageResponseDto;
import com.example.shopping.domain.product.dto.response.ProductUserResponseDto;
import com.example.shopping.domain.product.service.ProductUserService;
import com.example.shopping.domain.user.entity.User;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ProductUserController {

	private final ProductUserService productUserService;

	@GetMapping("/api/v1/product/{productId}/chase")
	public ResponseEntity<PageResponseDto<ProductUserResponseDto>> chaseMD(
		@AuthenticationPrincipal User user,
		@PathVariable Long productId,
		@PageableDefault(page = 1, size = 10) Pageable pageable
	) {
		Pageable convertPageable = PageRequest.of(pageable.getPageNumber() - 1, pageable.getPageSize());
		return ResponseEntity.ok(productUserService.chaseMD(user, productId, convertPageable));
	}
}
