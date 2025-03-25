package com.example.shopping.domain.product.controller;

import com.example.shopping.common.dto.PageResponseDto;
import com.example.shopping.domain.product.category.Category;
import com.example.shopping.domain.product.dto.request.ProductRequestDto;
import com.example.shopping.domain.product.dto.response.ProductResponseDto;
import com.example.shopping.domain.product.service.ProductService;
import com.example.shopping.domain.user.entity.User;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.RequiredArgsConstructor;

@RestController
@RestControllerAdvice
@RequiredArgsConstructor
public class ProductController {

	private final ProductService productService;

	@PostMapping("/api/v1/product")
	public ResponseEntity<ProductResponseDto> createProduct(
		@AuthenticationPrincipal User user,
		@RequestBody ProductRequestDto dto
	) {
		return ResponseEntity.ok(productService.createProduct(user, dto));
	}

	@GetMapping("/api/v1/product/{productId}")
	public ResponseEntity<ProductResponseDto> findProduct(
		@PathVariable Long productId
	) {
		return ResponseEntity.ok(productService.findProduct(productId));
	}

	@GetMapping("/api/v1/product")
	public ResponseEntity<PageResponseDto<ProductResponseDto>> findProducts(
		@RequestParam Category category, String keyword,
		@PageableDefault(page = 1, size = 10) Pageable pageable
	) {
		Pageable convertPageable = PageRequest.of(pageable.getPageNumber() - 1, pageable.getPageSize());
		return ResponseEntity.ok(productService.findProducts(category, keyword, convertPageable));
	}

	@PatchMapping("/api/v1/product/{productId}")
	public ResponseEntity<ProductResponseDto> updateProduct(
		@AuthenticationPrincipal User user,
		@PathVariable Long productId,
		@RequestBody ProductRequestDto dto
	) {
		return ResponseEntity.ok(productService.updateProduct(user, productId, dto));
	}

	@DeleteMapping("/api/v1/product/{productId}")
	public void deleteProduct(
		@AuthenticationPrincipal User user,
		@PathVariable Long productId
	) {
		productService.deleteProduct(user, productId);
	}
}
