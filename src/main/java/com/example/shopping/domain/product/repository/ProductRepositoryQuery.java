package com.example.shopping.domain.product.repository;

import com.example.shopping.domain.product.entity.Product;

import java.util.Optional;

public interface ProductRepositoryQuery {

    Optional<Product> findProductById(Long productId);
}
