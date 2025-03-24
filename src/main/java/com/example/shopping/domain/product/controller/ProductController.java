package com.example.shopping.domain.product.controller;

import com.example.shopping.domain.product.dto.request.ProductRequestDto;
import com.example.shopping.domain.product.dto.response.ProductResponseDto;
import com.example.shopping.domain.product.service.ProductService;
import com.example.shopping.domain.user.entity.User;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
}
