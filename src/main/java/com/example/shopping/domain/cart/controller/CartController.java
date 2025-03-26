package com.example.shopping.domain.cart.controller;

import com.example.shopping.common.dto.AuthUser;
import com.example.shopping.domain.cart.dto.request.CreateCartRequestDto;
import com.example.shopping.domain.cart.dto.response.GetCartResponseDto;
import com.example.shopping.domain.cart.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CartController {
	
	private final CartService cartService;
	
	@PostMapping("/api/v1/carts")
	public ResponseEntity<String> addCart(
		@AuthenticationPrincipal AuthUser authUser,
		@RequestParam Long productId,
		@Valid @RequestBody CreateCartRequestDto dto
	) {
		cartService.addCart(authUser.getId(), productId, dto);
		return ResponseEntity.ok("해당 상품이 장바구니에 등록 됐습니다.");
	}
	
	@GetMapping("/api/v1/carts")
	public ResponseEntity<List<GetCartResponseDto>> getCarts(
		@AuthenticationPrincipal AuthUser authUser
	) {
		return ResponseEntity.ok(cartService.getCarts(authUser.getId()));
	}
	
	@DeleteMapping("/api/v1/carts")
	public ResponseEntity<String> deleteItem(
		@AuthenticationPrincipal AuthUser authUser,
		@RequestParam Long productId
	) {
		cartService.deleteItem(authUser.getId(), productId);
		return ResponseEntity.ok("해당 상품이 장바구니에서 제거 됐습니다.");
	}
}
