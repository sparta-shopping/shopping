package com.example.shopping.domain.cart.controller;

import com.example.shopping.common.dto.PageResponseDto;
import com.example.shopping.domain.cart.dto.request.CreateCartRequestDto;
import com.example.shopping.domain.cart.dto.response.GetCartResponseDto;
import com.example.shopping.domain.cart.service.CartService;
import com.example.shopping.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CartController {
	
	private final CartService cartService;
	
	@PostMapping("/api/v1/carts")
	public ResponseEntity<String> addCart(
		@AuthenticationPrincipal User user,
		@RequestParam Long productId,
		CreateCartRequestDto dto
	) {
		cartService.addCart(user.getId(), productId, dto);
		return ResponseEntity.ok("해당 상품이 장바구니에 등록 됐습니다.");
	}
	
	@GetMapping("/api/v1/carts")
	public ResponseEntity<PageResponseDto<GetCartResponseDto>> getCarts(
		@AuthenticationPrincipal User user,
		@PageableDefault(page = 1, size = 10) Pageable pageable
	) {
		Pageable convertPageable = PageRequest.of(pageable.getPageNumber() - 1, pageable.getPageSize());
		return ResponseEntity.ok(cartService.getCarts(user.getId(), convertPageable));
	}
	
	@DeleteMapping("/api/v1/carts")
	public ResponseEntity<String> deleteItem(
		@AuthenticationPrincipal User user,
		@RequestParam Long productId
	) {
		cartService.deleteItem(user.getId(), productId);
		return ResponseEntity.ok("해당 상품이 장바구니에서 제거 됐습니다.");
	}
}
