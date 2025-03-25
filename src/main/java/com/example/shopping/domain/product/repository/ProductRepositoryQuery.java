package com.example.shopping.domain.product.repository;

import com.example.shopping.common.dto.PageResponseDto;
import com.example.shopping.domain.product.category.Category;
import com.example.shopping.domain.product.dto.response.ProductResponseDto;
import com.example.shopping.domain.product.entity.Product;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductRepositoryQuery {

    Optional<Product> findProductById(Long productId);

    Page<ProductResponseDto> findProductsByCategoryAndKeyword(
		Category category, String keyword, Pageable pageable
	);
}
