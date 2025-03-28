package com.example.shopping.domain.product.repository;

import com.example.shopping.domain.product.category.Category;
import com.example.shopping.domain.product.dto.response.ProductResponseDto;
import com.example.shopping.domain.product.entity.Product;

import java.util.List;
import java.util.Optional;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface ProductRepositoryQuery {

	Optional<Product> findProductById(Long productId);

	Page<ProductResponseDto> findProductsByCategoryAndKeyword(
		Category category, String keyword, Pageable pageable
	);

}

